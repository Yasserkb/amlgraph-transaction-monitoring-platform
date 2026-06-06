package com.amlgraph.customer.api.controller;

import com.amlgraph.common.api.ApiResponse;
import com.amlgraph.common.api.PagedResponse;
import com.amlgraph.common.domain.RiskLevel;
import com.amlgraph.customer.api.dto.CustomerResponse;
import com.amlgraph.customer.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public PagedResponse<CustomerResponse> search(@RequestParam(required = false) RiskLevel riskLevel,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return customerService.search(riskLevel, page, size);
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> get(@PathVariable UUID id) {
        return ApiResponse.of(customerService.get(id));
    }
}
