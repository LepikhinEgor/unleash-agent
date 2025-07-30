package ru.baldenna.unleashagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.baldenna.unleashagent.dto.Feature;
import ru.baldenna.unleashagent.dto.Tag;

import java.util.List;

@ConfigurationProperties("unleash")
public record UnleashConfig(

    List<Feature> features,

    List<Tag> tags

) {}