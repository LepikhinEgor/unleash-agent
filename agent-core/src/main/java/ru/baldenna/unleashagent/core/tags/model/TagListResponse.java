package ru.baldenna.unleashagent.core.tags.model;

import java.util.List;

public record TagListResponse (
        List<Tag> tags
) {}