package ru.baldenna.unleashagent.core.segments.model;

import java.util.List;

public record CreateSegmentRequest(
        String name,
        String description,
        String project,
        List<FeatureConstraint> constraints
) {
}
