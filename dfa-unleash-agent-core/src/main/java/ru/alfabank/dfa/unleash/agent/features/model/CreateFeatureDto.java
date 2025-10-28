package ru.alfabank.dfa.unleash.agent.features.model;

import ru.alfabank.dfa.unleash.agent.tags.model.Tag;

import java.util.HashSet;

public record CreateFeatureDto(
        String name,
        String type,
        String description,
        HashSet<Tag> tags
) {
}

