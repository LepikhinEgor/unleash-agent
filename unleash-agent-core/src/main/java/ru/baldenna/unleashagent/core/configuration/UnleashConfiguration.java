package ru.baldenna.unleashagent.core.configuration;

import ru.baldenna.unleashagent.core.contextfields.model.ContextField;
import ru.baldenna.unleashagent.core.apitokens.model.ApiToken;
import ru.baldenna.unleashagent.core.segments.model.Segment;
import ru.baldenna.unleashagent.core.tags.model.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Configuration for whole Unleash app.
 * For example tags not linked for certain project and need to be configured independently
 */
public record UnleashConfiguration(
        List<Tag> tags,
        List<Segment> segments,
        List<ContextField> contextFields,
        Map<String, UnleashProjectConfiguration> projects,
        List<ApiToken> apiTokens
) {

    private static final String DEFAULT_PROJECT_NAME = "default";

    @Override
    public List<Tag> tags() {
        return Optional.ofNullable(tags).orElse(emptyList());
    }

    @Override
    public List<Segment> segments() {
        return Optional.ofNullable(segments).orElse(emptyList());
    }

    @Override
    public List<ContextField> contextFields() {
        return Optional.ofNullable(contextFields).orElse(emptyList());
    }

    @Override
    public Map<String, UnleashProjectConfiguration> projects() {
        return Optional.ofNullable(projects)
                .orElse(Map.of(DEFAULT_PROJECT_NAME, new UnleashProjectConfiguration(emptyList(), emptyList())));
    }

    public List<ApiToken> apiTokens() {
        return Optional.ofNullable(apiTokens).orElse(emptyList());
    }
}
