package ru.baldenna.unleashagent.core.segments.model;

import java.util.List;

public record Segment(
        int id,
        String name,
        String description,
        List<SegmentConstraint> constraints,
        String project
) {

    public Segment(Segment source, int id) {
        this(id, source.name, source.description, source.constraints, source.project);
    }
}
