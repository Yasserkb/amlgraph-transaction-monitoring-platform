package com.amlgraph.alert.repository;

import com.amlgraph.alert.domain.AlertEntity;
import com.amlgraph.common.domain.AlertStatus;
import com.amlgraph.common.domain.Severity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<AlertEntity, UUID> {
    @Query("""
            select a from AlertEntity a
            where (:severity is null or a.severity = :severity)
              and (:status is null or a.status = :status)
            order by a.triggeredAt desc
            """)
    Page<AlertEntity> search(Severity severity, AlertStatus status, Pageable pageable);
}
