package com.amlgraph.customer.api.mapper;

import com.amlgraph.customer.api.dto.CustomerResponse;
import com.amlgraph.customer.domain.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerResponse toResponse(CustomerEntity entity) {
        return new CustomerResponse(entity.getId(), entity.getFullName(), entity.getNationality(), entity.getCountryOfResidence(),
                entity.getRiskScore(), entity.getRiskLevel(), entity.getKycStatus(), entity.isPep(), entity.isSanctioned(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
