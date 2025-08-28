package ru.baldenna.unleashagent.core.segments.model;

import java.util.List;

public record SegmentListResponse(
        List<Segment> segments
) {
}
