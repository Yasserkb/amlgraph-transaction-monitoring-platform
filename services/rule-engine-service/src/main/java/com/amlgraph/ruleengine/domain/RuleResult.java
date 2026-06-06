package com.amlgraph.ruleengine.domain;

import com.amlgraph.common.domain.Severity;

public record RuleResult(String ruleId, String ruleName, Severity severity, String description) {}
