package com.amlgraph.ruleengine.service;

import com.amlgraph.common.domain.Severity;
import com.amlgraph.common.event.TransactionCreatedEvent;
import com.amlgraph.ruleengine.domain.RuleDefinition;
import com.amlgraph.ruleengine.domain.RuleResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Service
public class AmlRuleEngine {
    private static final BigDecimal LARGE_TRANSACTION_THRESHOLD = new BigDecimal("10000.00");
    private static final BigDecimal CRITICAL_TRANSACTION_THRESHOLD = new BigDecimal("50000.00");
    private static final Set<String> HIGH_RISK_COUNTRIES = Set.of("AE", "IR", "KP", "MM");

    public List<RuleDefinition> rules() {
        return List.of(
                new RuleDefinition("R001", "Large Cash / Wire Transaction", "Single transaction above EUR 10,000", Severity.HIGH, true),
                new RuleDefinition("R004", "High-Risk Country Flow", "Origin or destination country is high risk", Severity.HIGH, true),
                new RuleDefinition("R010", "Unusual Hour", "Large transaction executed between 02:00 and 05:00 UTC", Severity.MEDIUM, true)
        );
    }

    public List<RuleResult> evaluate(TransactionCreatedEvent event) {
        var results = new java.util.ArrayList<RuleResult>();

        if (event.amount().compareTo(CRITICAL_TRANSACTION_THRESHOLD) >= 0) {
            results.add(new RuleResult(
                    "R001",
                    "Large Cash / Wire Transaction",
                    Severity.CRITICAL,
                    "Critical transaction amount detected: " + event.amount() + " " + event.currency()
            ));
        } else if (event.amount().compareTo(LARGE_TRANSACTION_THRESHOLD) >= 0) {
            results.add(new RuleResult(
                    "R001",
                    "Large Cash / Wire Transaction",
                    Severity.HIGH,
                    "Large transaction above reporting threshold: " + event.amount() + " " + event.currency()
            ));
        }

        if (isHighRisk(event.originCountry()) || isHighRisk(event.destinationCountry())) {
            results.add(new RuleResult(
                    "R004",
                    "High-Risk Country Flow",
                    Severity.HIGH,
                    "Transaction involves high-risk jurisdiction: " + event.originCountry() + " → " + event.destinationCountry()
            ));
        }

        var hour = event.executedAt().atZone(ZoneOffset.UTC).getHour();
        if (event.amount().compareTo(new BigDecimal("5000.00")) >= 0 && hour >= 2 && hour <= 5) {
            results.add(new RuleResult(
                    "R010",
                    "Unusual Hour",
                    Severity.MEDIUM,
                    "High-value transaction executed at unusual hour UTC: " + hour
            ));
        }

        return results;
    }

    private boolean isHighRisk(String country) {
        return country != null && HIGH_RISK_COUNTRIES.contains(country.toUpperCase());
    }
}
