package ru.alfabank.dfa.unleash.agent.segments.model;

import java.util.List;

public record UpdateSegmentRequest(
        String name,
        String description,
        String project,
        List<FeatureConstraint> constraints
) {
}
