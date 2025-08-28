package ru.baldenna.unleashagent.core.segments.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record FeatureConstraint(
        String contextName,
        String operator,
        boolean caseInsensitive,
        boolean inverted,
        List<String> values,
        String value
) {

    @Override
    public List<String> values() {
        return Optional.ofNullable(values).orElse(Collections.emptyList());
    }
}
