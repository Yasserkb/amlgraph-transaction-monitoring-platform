package com.amlgraph.common.event;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope<T>(String eventType, UUID eventId, Instant occurredAt, T payload) {
    public static <T> EventEnvelope<T> of(String eventType, T payload) {
        return new EventEnvelope<>(eventType, UUID.randomUUID(), Instant.now(), payload);
    }
}
