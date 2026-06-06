package com.amlgraph.customer.service;

import com.amlgraph.common.api.PagedResponse;
import com.amlgraph.common.domain.RiskLevel;
import com.amlgraph.common.event.AlertCreatedEvent;
import com.amlgraph.common.exception.NotFoundException;
import com.amlgraph.customer.api.dto.CustomerResponse;
import com.amlgraph.customer.api.mapper.CustomerMapper;
import com.amlgraph.customer.domain.CustomerRiskCalculator;
import com.amlgraph.customer.repository.CustomerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public CustomerService(CustomerRepository repository, CustomerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public PagedResponse<CustomerResponse> search(RiskLevel riskLevel, int page, int size) {
        var result = repository.search(riskLevel, PageRequest.of(page, size));
        return PagedResponse.of(result.getContent().stream().map(mapper::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    @Transactional
    public void applyAlertImpact(AlertCreatedEvent event) {
        repository.findById(event.customerId()).ifPresent(customer -> {
            customer.applyAlertImpact(CustomerRiskCalculator.alertDelta(event.severity()));
            repository.save(customer);
        });
    }
}
