package ru.baldenna.unleashagent.core.tags.model;

import ru.baldenna.unleashagent.tags.model.Tag;

import java.util.List;

public record TagListResponse (
        List<Tag> tags
) {}