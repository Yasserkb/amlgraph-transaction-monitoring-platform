package com.amlgraph.alert.api.dto;

import com.amlgraph.common.domain.AlertStatus;
import com.amlgraph.common.domain.Severity;

import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        UUID transactionId,
        UUID customerId,
        String ruleId,
        Severity severity,
        AlertStatus status,
        String description,
        Instant triggeredAt,
        Instant closedAt
) {}
