package ru.alfabank.dfa.unleash.agent.unleashagent.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.alfabank.dfa.unleash.agent.configuration.UnleashConfiguration;

@ConfigurationProperties("unleash")
public record UnleashConfigProperties(
        String url,
        String username,
        String password,
        UnleashConfiguration configuration
) {
}
