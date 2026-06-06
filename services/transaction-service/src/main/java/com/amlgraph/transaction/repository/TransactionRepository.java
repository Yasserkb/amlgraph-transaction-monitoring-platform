package com.amlgraph.transaction.repository;

import com.amlgraph.common.domain.TransactionStatus;
import com.amlgraph.transaction.domain.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    @Query("""
            select t from TransactionEntity t
            where (:customerId is null or t.customerId = :customerId)
              and (:status is null or t.status = :status)
            order by t.executedAt desc
            """)
    Page<TransactionEntity> search(UUID customerId, TransactionStatus status, Pageable pageable);
}
