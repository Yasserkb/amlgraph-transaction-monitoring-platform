package com.amlgraph.customer.domain;

import com.amlgraph.common.domain.RiskLevel;
import com.amlgraph.common.domain.Severity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRiskCalculatorTest {
    @Test
    void toRiskLevel_shouldMapScoreBoundaries() {
        assertThat(CustomerRiskCalculator.toRiskLevel(10)).isEqualTo(RiskLevel.LOW);
        assertThat(CustomerRiskCalculator.toRiskLevel(40)).isEqualTo(RiskLevel.MEDIUM);
        assertThat(CustomerRiskCalculator.toRiskLevel(70)).isEqualTo(RiskLevel.HIGH);
        assertThat(CustomerRiskCalculator.toRiskLevel(90)).isEqualTo(RiskLevel.CRITICAL);
    }

    @Test
    void alertDelta_shouldGiveHigherImpactToCriticalAlerts() {
        assertThat(CustomerRiskCalculator.alertDelta(Severity.CRITICAL))
                .isGreaterThan(CustomerRiskCalculator.alertDelta(Severity.HIGH));
    }
}
