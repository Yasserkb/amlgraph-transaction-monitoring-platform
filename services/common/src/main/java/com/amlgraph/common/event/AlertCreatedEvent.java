package com.amlgraph.common.event;

import com.amlgraph.common.domain.Severity;
import java.time.Instant;
import java.util.UUID;

public record AlertCreatedEvent(
        UUID alertId,
        UUID transactionId,
        UUID customerId,
        String ruleId,
        Severity severity,
        String description,
        Instant triggeredAt
) {}
