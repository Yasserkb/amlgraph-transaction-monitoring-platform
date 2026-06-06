package com.amlgraph.alert.service;

import com.amlgraph.alert.api.dto.*;
import com.amlgraph.alert.api.mapper.AlertMapper;
import com.amlgraph.alert.domain.AlertEntity;
import com.amlgraph.alert.domain.CaseEntity;
import com.amlgraph.alert.repository.AlertRepository;
import com.amlgraph.alert.repository.CaseRepository;
import com.amlgraph.common.api.PagedResponse;
import com.amlgraph.common.domain.*;
import com.amlgraph.common.event.AlertCreatedEvent;
import com.amlgraph.common.exception.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AlertCaseService {
    private final AlertRepository alertRepository;
    private final CaseRepository caseRepository;
    private final AlertMapper mapper;

    public AlertCaseService(AlertRepository alertRepository, CaseRepository caseRepository, AlertMapper mapper) {
        this.alertRepository = alertRepository;
        this.caseRepository = caseRepository;
        this.mapper = mapper;
    }

    @Transactional
    public AlertResponse handleAlertCreated(AlertCreatedEvent event) {
        var alert = new AlertEntity(
                event.alertId(), event.transactionId(), event.customerId(), event.ruleId(), event.severity(),
                AlertStatus.OPEN, event.description(), event.triggeredAt(), null
        );
        var savedAlert = alertRepository.save(alert);
        if (event.severity() == Severity.HIGH || event.severity() == Severity.CRITICAL) {
            caseRepository.save(new CaseEntity(
                    UUID.randomUUID(), savedAlert.getId(), null, CaseStatus.NEW,
                    event.severity() == Severity.CRITICAL, Instant.now(), null
            ));
        }
        return mapper.toResponse(savedAlert);
    }

    @Transactional(readOnly = true)
    public PagedResponse<AlertResponse> searchAlerts(Severity severity, AlertStatus status, int page, int size) {
        var result = alertRepository.search(severity, status, PageRequest.of(page, size));
        return PagedResponse.of(result.getContent().stream().map(mapper::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public AlertResponse getAlert(UUID id) {
        return alertRepository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Alert not found: " + id));
    }

    @Transactional
    public AlertResponse updateAlertStatus(UUID id, AlertStatus status) {
        var alert = alertRepository.findById(id).orElseThrow(() -> new NotFoundException("Alert not found: " + id));
        alert.updateStatus(status);
        return mapper.toResponse(alertRepository.save(alert));
    }

    @Transactional(readOnly = true)
    public PagedResponse<CaseResponse> searchCases(CaseStatus status, int page, int size) {
        var result = caseRepository.search(status, PageRequest.of(page, size));
        return PagedResponse.of(result.getContent().stream().map(mapper::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public CaseResponse getCase(UUID id) {
        return caseRepository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Case not found: " + id));
    }

    @Transactional
    public CaseResponse assignCase(UUID id, UUID analystId) {
        var caseEntity = caseRepository.findById(id).orElseThrow(() -> new NotFoundException("Case not found: " + id));
        caseEntity.assign(analystId);
        return mapper.toResponse(caseRepository.save(caseEntity));
    }

    @Transactional
    public CaseResponse updateCaseStatus(UUID id, CaseStatus status) {
        var caseEntity = caseRepository.findById(id).orElseThrow(() -> new NotFoundException("Case not found: " + id));
        caseEntity.updateStatus(status);
        return mapper.toResponse(caseRepository.save(caseEntity));
    }

    @Transactional
    public CaseResponse addCaseNote(UUID id, UUID authorId, String content) {
        var caseEntity = caseRepository.findById(id).orElseThrow(() -> new NotFoundException("Case not found: " + id));
        caseEntity.addNote(authorId, content);
        return mapper.toResponse(caseRepository.save(caseEntity));
    }
}
