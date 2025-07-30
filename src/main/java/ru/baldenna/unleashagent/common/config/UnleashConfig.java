package ru.baldenna.unleashagent.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.baldenna.unleashagent.features.Feature;
import ru.baldenna.unleashagent.tags.Tag;

import java.util.List;

@ConfigurationProperties("unleash")
public record UnleashConfig(

    List<Feature> features,

    List<Tag> tags

) {}