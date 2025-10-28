package ru.alfabank.dfa.unleash.agent.segments.model;

import java.util.List;

public record Segment(
        int id,
        String name,
        String description,
        List<FeatureConstraint> constraints,
        String project
) {

    public Segment copyWithId(int id) {
        return new Segment(id, name, description, constraints, project);
    }
}
