package com.amlgraph.common.json;

import com.amlgraph.common.event.EventEnvelope;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public final class Jsons {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private Jsons() {}

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize JSON", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON payload", e);
        }
    }

    public static <T> EventEnvelope<T> fromEvent(String json, Class<T> payloadType) {
        try {
            JsonNode root = MAPPER.readTree(json);
            T payload = MAPPER.treeToValue(root.get("payload"), payloadType);
            return new EventEnvelope<>(
                    root.get("eventType").asText(),
                    UUID.fromString(root.get("eventId").asText()),
                    Instant.parse(root.get("occurredAt").asText()),
                    payload
            );
        } catch (IOException | RuntimeException e) {
            throw new IllegalArgumentException("Invalid event envelope", e);
        }
    }
}
