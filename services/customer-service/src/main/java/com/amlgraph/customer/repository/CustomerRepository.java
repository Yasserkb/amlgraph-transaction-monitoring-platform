package com.amlgraph.customer.repository;

import com.amlgraph.common.domain.RiskLevel;
import com.amlgraph.customer.domain.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    @Query("""
            select c from CustomerEntity c
            where (:riskLevel is null or c.riskLevel = :riskLevel)
            order by c.riskScore desc
            """)
    Page<CustomerEntity> search(RiskLevel riskLevel, Pageable pageable);
}
