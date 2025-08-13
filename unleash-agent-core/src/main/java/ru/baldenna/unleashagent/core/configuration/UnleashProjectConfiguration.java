package ru.baldenna.unleashagent.core.configuration;

import ru.baldenna.unleashagent.core.features.model.Feature;
import ru.baldenna.unleashagent.core.tags.model.Tag;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public record UnleashProjectConfiguration (

        List<Feature> features

) {
    @Override
    public List<Feature> features() {
        return Optional.ofNullable(features).orElse(emptyList());
    }
}