package ru.alfabank.dfa.unleash.agent.projects;

public record Project(
        String id,
        String name,
        Integer technicalDebt,
        Integer featureCount,
        Integer memberCount
) {
}
