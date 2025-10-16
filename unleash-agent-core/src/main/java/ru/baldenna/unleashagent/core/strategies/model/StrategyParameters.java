package ru.baldenna.unleashagent.core.strategies.model;

public record StrategyParameters(
        String rollout,
        String stickiness,
        String groupId
) {
}
