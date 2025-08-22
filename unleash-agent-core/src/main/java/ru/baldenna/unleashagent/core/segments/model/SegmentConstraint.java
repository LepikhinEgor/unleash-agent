package ru.baldenna.unleashagent.core.segments.model;

import java.util.List;

public record SegmentConstraint(
        String contextName,
        String operator,
        boolean caseInsensitive,
        boolean inverted,
        List<String> values
) {
}