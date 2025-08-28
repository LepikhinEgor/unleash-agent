package ru.baldenna.unleashagent.core.strategies.model;

public record Variant(
        String name,
        int weight,
        String weightType,
        String stickiness,
        VariantPayload payload
) {
}
