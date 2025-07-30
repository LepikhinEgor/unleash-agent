package ru.baldenna.unleashagent.dto;

import java.util.List;

public record TagListResponse (
        List<Tag> tags
) {}