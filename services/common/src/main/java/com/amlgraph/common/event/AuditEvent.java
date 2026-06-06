package com.amlgraph.common.event;

import java.time.Instant;
import java.util.UUID;

public record AuditEvent(
        UUID id,
        String entityType,
        UUID entityId,
        String action,
        String performedBy,
        String previousState,
        String newState,
        Instant timestamp
) {}
