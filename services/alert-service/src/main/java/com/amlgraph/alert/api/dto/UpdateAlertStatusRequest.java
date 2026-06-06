package com.amlgraph.alert.api.dto;

import com.amlgraph.common.domain.AlertStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAlertStatusRequest(@NotNull AlertStatus status) {}
