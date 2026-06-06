package com.amlgraph.alert.repository;

import com.amlgraph.alert.domain.CaseEntity;
import com.amlgraph.common.domain.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CaseRepository extends JpaRepository<CaseEntity, UUID> {
    @Query("""
            select c from CaseEntity c
            where (:status is null or c.status = :status)
            order by c.openedAt desc
            """)
    Page<CaseEntity> search(CaseStatus status, Pageable pageable);
}
