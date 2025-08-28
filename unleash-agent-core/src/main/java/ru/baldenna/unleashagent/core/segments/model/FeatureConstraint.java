package ru.baldenna.unleashagent.core.segments.model;

import java.util.List;

public record FeatureConstraint(
        String contextName,
        String operator,
        boolean caseInsensitive,
        boolean inverted,
        List<String> values
) {
}
