package ru.baldenna.unleashagent.tags.model;

import java.util.List;

public record TagListResponse (
        List<Tag> tags
) {}