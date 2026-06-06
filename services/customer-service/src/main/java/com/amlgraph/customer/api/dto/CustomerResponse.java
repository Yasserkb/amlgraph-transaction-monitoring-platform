package com.amlgraph.customer.api.dto;

import com.amlgraph.common.domain.RiskLevel;
import com.amlgraph.customer.domain.KycStatus;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String fullName,
        String nationality,
        String countryOfResidence,
        int riskScore,
        RiskLevel riskLevel,
        KycStatus kycStatus,
        boolean pep,
        boolean sanctioned,
        Instant createdAt,
        Instant updatedAt
) {}
