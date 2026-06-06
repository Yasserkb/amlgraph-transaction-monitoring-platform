package com.amlgraph.ruleengine.domain;

import com.amlgraph.common.domain.Severity;

public record RuleDefinition(String id, String name, String description, Severity severity, boolean enabled) {}
