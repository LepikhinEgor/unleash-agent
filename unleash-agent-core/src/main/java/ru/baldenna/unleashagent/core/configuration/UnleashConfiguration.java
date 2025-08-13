package ru.baldenna.unleashagent.core.configuration;

import ru.baldenna.unleashagent.core.tags.model.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

public record UnleashConfiguration(

        List<Tag> tags,
        Map<String, UnleashProjectConfiguration> projects
) {

    private static final String DEFAULT_PROJECT_NAME = "default";

    @Override
    public List<Tag> tags() {
        return Optional.ofNullable(tags).orElse(emptyList());
    }

    @Override
    public Map<String, UnleashProjectConfiguration> projects() {
        return Optional.ofNullable(projects)
                .orElse(Map.of(DEFAULT_PROJECT_NAME, new UnleashProjectConfiguration(emptyList())));
    }
}