package ru.baldenna.unleashagent.core.configuration;

import ru.baldenna.unleashagent.core.features.model.Feature;
import ru.baldenna.unleashagent.core.tags.model.Tag;

import java.util.List;

public record UnleashConfiguration(

    List<Feature> features,

    List<Tag> tags

) {}