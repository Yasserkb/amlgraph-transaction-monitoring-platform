package com.amlgraph.customer.domain;

import com.amlgraph.common.domain.RiskLevel;
import com.amlgraph.common.domain.Severity;

public final class CustomerRiskCalculator {
    private CustomerRiskCalculator() {}

    public static int alertDelta(Severity severity) {
        return switch (severity) {
            case LOW -> 2;
            case MEDIUM -> 5;
            case HIGH -> 10;
            case CRITICAL -> 20;
        };
    }

    public static RiskLevel toRiskLevel(int score) {
        if (score <= 25) return RiskLevel.LOW;
        if (score <= 50) return RiskLevel.MEDIUM;
        if (score <= 75) return RiskLevel.HIGH;
        return RiskLevel.CRITICAL;
    }
}
