package com.amlgraph.common.api;

import java.time.Instant;

public record ErrorResponse(ErrorBody error) {
    public static ErrorResponse of(String code, String message, String traceId) {
        return new ErrorResponse(new ErrorBody(code, message, Instant.now(), traceId));
    }

    public record ErrorBody(String code, String message, Instant timestamp, String traceId) {}
}
