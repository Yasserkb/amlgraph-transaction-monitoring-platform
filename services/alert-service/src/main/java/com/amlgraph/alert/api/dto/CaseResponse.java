package com.amlgraph.alert.api.dto;

import com.amlgraph.common.domain.CaseStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CaseResponse(
        UUID id,
        UUID alertId,
        UUID assignedAnalystId,
        CaseStatus status,
        boolean strRequired,
        Instant openedAt,
        Instant closedAt,
        List<CaseNoteResponse> notes
) {}
