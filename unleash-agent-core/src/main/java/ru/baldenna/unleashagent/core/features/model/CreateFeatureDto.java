package ru.baldenna.unleashagent.core.features.model;

import ru.baldenna.unleashagent.core.tags.model.Tag;

import java.util.HashSet;

public record CreateFeatureDto(
        String name,
        String type,
        String description,
        HashSet<Tag> tags
) {
}

