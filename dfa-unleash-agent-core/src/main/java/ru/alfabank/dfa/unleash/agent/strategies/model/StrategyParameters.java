package ru.alfabank.dfa.unleash.agent.strategies.model;

public record StrategyParameters(
        String rollout,
        String stickiness,
        String groupId
) {
}
