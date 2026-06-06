package com.amlgraph.alert.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AddCaseNoteRequest(@NotNull UUID authorId, @NotBlank @Size(max = 2000) String content) {}
