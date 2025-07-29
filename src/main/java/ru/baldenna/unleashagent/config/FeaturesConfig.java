package ru.baldenna.unleashagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.baldenna.unleashagent.dto.Feature;

import java.util.List;

@ConfigurationProperties("unleash")
public record FeaturesConfig (

    List<Feature> features

) {}