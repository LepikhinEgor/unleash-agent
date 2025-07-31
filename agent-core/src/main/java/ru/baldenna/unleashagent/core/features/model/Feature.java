package ru.baldenna.unleashagent.core.features.model;

import ru.baldenna.unleashagent.core.tags.model.Tag;

import java.util.HashSet;

public record Feature(
         String type,
         String description,
         String project,
         boolean favorite,
         String name,
         String createdAt,
         boolean stale,
         String archivedAt,
         boolean impressionData,
         String lastSeenAt,
         String dependencyType,
         FeatureLifecycle lifecycle,
         HashSet<Tag> tags
) {

    @Override
    public HashSet<Tag> tags() {
        return tags == null? new HashSet<>() : tags;
    }
}
