package ru.baldenna.unleashagent.starter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.baldenna.unleashagent.core.UnleashAgent;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.client.UnleashClientFactory;
import ru.baldenna.unleashagent.core.common.UnleashUpdaters;
import ru.baldenna.unleashagent.core.common.UnleashUpdatersFactory;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.configuration.YamlConfigurationParser;

@Configuration
@EnableConfigurationProperties(UnleashConfigProperties.class)
public class UnleashAgentAutoConfiguration {

    @Bean
    UnleashClient unleashClient(UnleashConfigProperties unleashConfigProperties) {
        var clientFactory = new UnleashClientFactory();
        return clientFactory.buildClient(unleashConfigProperties.url());
    }

    @Bean
    UnleashSessionManager unleashSessionManager(UnleashClient unleashClient,
                                                UnleashConfigProperties unleashProperties) {
        return new UnleashSessionManager(
                unleashClient,
                unleashProperties.username(),
                unleashProperties.password()
        );
    }

    @Bean
    UnleashUpdatersFactory unleashUpdatersFactory(UnleashClient unleashClient,
                                                  UnleashSessionManager unleashSessionManager) {
        return new UnleashUpdatersFactory(unleashClient, unleashSessionManager);
    }

    @Bean
    UnleashUpdaters unleashUpdaters(UnleashUpdatersFactory unleashUpdatersFactory) {
        return unleashUpdatersFactory.buildUpdaters();
    }

    @Bean
    UnleashAgent unleashAgent(UnleashUpdaters unleashUpdaters) {
        return new UnleashAgent(unleashUpdaters);
    }

    @Bean
    UnleashConfiguration unleashConfiguration(UnleashConfigProperties unleashConfigProperties) {
        return unleashConfigProperties.configuration();
    }

    @Bean
    YamlConfigurationParser yamlConfigurationParser() {
        return new YamlConfigurationParser();
    }


}
