package com.amlgraph.alert.service;

import com.amlgraph.alert.api.mapper.AlertMapper;
import com.amlgraph.alert.domain.AlertEntity;
import com.amlgraph.alert.domain.CaseEntity;
import com.amlgraph.alert.repository.AlertRepository;
import com.amlgraph.alert.repository.CaseRepository;
import com.amlgraph.common.domain.Severity;
import com.amlgraph.common.event.AlertCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertCaseServiceTest {
    @Mock AlertRepository alertRepository;
    @Mock CaseRepository caseRepository;

    @Test
    void handleAlertCreated_shouldCreateInvestigationCaseForHighAlert() {
        var service = new AlertCaseService(alertRepository, caseRepository, new AlertMapper());
        var event = new AlertCreatedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "R001", Severity.HIGH, "large transaction", Instant.now());
        when(alertRepository.save(any(AlertEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(caseRepository.save(any(CaseEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.handleAlertCreated(event);

        assertThat(response.id()).isEqualTo(event.alertId());
        var caseCaptor = ArgumentCaptor.forClass(CaseEntity.class);
        verify(caseRepository).save(caseCaptor.capture());
        assertThat(caseCaptor.getValue().getAlertId()).isEqualTo(event.alertId());
        assertThat(caseCaptor.getValue().isStrRequired()).isFalse();
    }

    @Test
    void handleAlertCreated_shouldMarkCriticalCasesAsStrRequired() {
        var service = new AlertCaseService(alertRepository, caseRepository, new AlertMapper());
        var event = new AlertCreatedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "R001", Severity.CRITICAL, "critical transaction", Instant.now());
        when(alertRepository.save(any(AlertEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.handleAlertCreated(event);

        var caseCaptor = ArgumentCaptor.forClass(CaseEntity.class);
        verify(caseRepository).save(caseCaptor.capture());
        assertThat(caseCaptor.getValue().isStrRequired()).isTrue();
    }
}
