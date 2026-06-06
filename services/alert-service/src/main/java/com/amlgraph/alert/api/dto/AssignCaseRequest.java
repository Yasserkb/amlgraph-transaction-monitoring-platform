package com.amlgraph.alert.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignCaseRequest(@NotNull UUID analystId) {}
