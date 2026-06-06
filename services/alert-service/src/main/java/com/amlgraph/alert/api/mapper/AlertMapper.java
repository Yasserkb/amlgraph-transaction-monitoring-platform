package com.amlgraph.alert.api.mapper;

import com.amlgraph.alert.api.dto.*;
import com.amlgraph.alert.domain.*;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {
    public AlertResponse toResponse(AlertEntity entity) {
        return new AlertResponse(entity.getId(), entity.getTransactionId(), entity.getCustomerId(), entity.getRuleId(),
                entity.getSeverity(), entity.getStatus(), entity.getDescription(), entity.getTriggeredAt(), entity.getClosedAt());
    }

    public CaseResponse toResponse(CaseEntity entity) {
        var notes = entity.getNotes().stream().map(this::toResponse).toList();
        return new CaseResponse(entity.getId(), entity.getAlertId(), entity.getAssignedAnalystId(), entity.getStatus(),
                entity.isStrRequired(), entity.getOpenedAt(), entity.getClosedAt(), notes);
    }

    public CaseNoteResponse toResponse(CaseNoteEntity note) {
        return new CaseNoteResponse(note.getId(), note.getAuthorId(), note.getContent(), note.getCreatedAt());
    }
}
