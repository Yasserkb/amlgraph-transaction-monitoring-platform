package com.amlgraph.alert.api.dto;

import java.time.Instant;
import java.util.UUID;

public record CaseNoteResponse(UUID id, UUID authorId, String content, Instant createdAt) {}
