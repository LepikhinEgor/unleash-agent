package ru.baldenna.unleashagent.dto;

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
}
