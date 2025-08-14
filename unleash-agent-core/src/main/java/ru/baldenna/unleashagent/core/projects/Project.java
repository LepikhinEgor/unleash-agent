package ru.baldenna.unleashagent.core.projects;

public record Project(
        String id,
        String name,
        Integer technicalDebt,
        Integer featureCount,
        Integer memberCount
) {
}
