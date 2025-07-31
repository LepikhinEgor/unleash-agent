package ru.baldenna.unleashagent.core.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.baldenna.unleashagent.features.model.Feature;
import ru.baldenna.unleashagent.tags.model.Tag;

import java.util.List;

@ConfigurationProperties("unleash")
public record UnleashConfig(

    List<Feature> features,

    List<Tag> tags

) {}