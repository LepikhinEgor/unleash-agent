package ru.baldenna.unleashagent.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;

@ConfigurationProperties("unleash")
public record UnleashConfigProperties(
        String url,
        String username,
        String password,
        UnleashConfiguration configuration
) {
}
