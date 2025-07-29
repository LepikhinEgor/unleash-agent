package ru.baldenna.unleashagent.core.configuration;

import ru.baldenna.unleashagent.core.features.model.Feature;
import ru.baldenna.unleashagent.core.projects.ProjectEnvironment;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Configuration associated with a project in Unleash.
 * There can be several projects in Unleash that are used as namespaces.
 */
public record UnleashProjectConfiguration(

        List<Feature> features,
        List<ProjectEnvironment> environments

) {
    @Override
    public List<Feature> features() {
        return Optional.ofNullable(features).orElse(emptyList());
    }

    @Override
    public List<ProjectEnvironment> environments() {
        return Optional.ofNullable(environments).orElse(emptyList());
    }
}
