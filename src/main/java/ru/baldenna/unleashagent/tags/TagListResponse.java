package ru.baldenna.unleashagent.tags;

import java.util.List;

public record TagListResponse (
        List<Tag> tags
) {}