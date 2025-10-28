package ru.alfabank.dfa.unleash.agent.features.model;

import ru.alfabank.dfa.unleash.agent.tags.model.Tag;

import java.util.HashSet;

public record Feature(
        String name,
        String type,
        String description,
        boolean favorite,
        boolean stale,
        HashSet<Tag> tags
) {

    @Override
    public HashSet<Tag> tags() {
        return tags == null ? new HashSet<>() : tags;
    }
}
