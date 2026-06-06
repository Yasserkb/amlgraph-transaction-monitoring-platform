package com.amlgraph.alert.api.dto;

import com.amlgraph.common.domain.CaseStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCaseStatusRequest(@NotNull CaseStatus status) {}
