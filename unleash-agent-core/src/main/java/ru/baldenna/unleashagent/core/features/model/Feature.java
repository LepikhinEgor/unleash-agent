package ru.baldenna.unleashagent.core.features.model;

import ru.baldenna.unleashagent.core.tags.model.Tag;

import java.util.HashSet;

public record Feature(
        String name,
        String type,
        String description,
        String project,
        boolean favorite,
        boolean stale,
        HashSet<Tag> tags
) {

    @Override
    public HashSet<Tag> tags() {
        return tags == null ? new HashSet<>() : tags;
    }
}
