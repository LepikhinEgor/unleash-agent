package ru.baldenna.unleashagent.core.segments.model;

public record CreateSegmentRequest(
        String name,
        String description,
        String project,
        List<Constraint> constraints
) {
    public record Constraint(
            String contextName,
            String operator,
            boolean caseInsensitive,
            boolean inverted,
            List<String> values
    ) {
        // Nested record for constraints
    }
}
