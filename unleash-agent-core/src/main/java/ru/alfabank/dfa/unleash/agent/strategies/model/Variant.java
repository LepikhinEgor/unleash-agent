package ru.alfabank.dfa.unleash.agent.strategies.model;

public record Variant(
        String name,
        int weight,
        String weightType,
        String stickiness,
        VariantPayload payload
) {
}
