package ru.alfabank.dfa.unleash.agent.configuration;

import ru.alfabank.dfa.unleash.agent.features.model.Feature;
import ru.alfabank.dfa.unleash.agent.projects.ProjectEnvironment;

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
