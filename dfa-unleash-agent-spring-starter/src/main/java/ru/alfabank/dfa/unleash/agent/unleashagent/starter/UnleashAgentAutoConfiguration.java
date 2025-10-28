package ru.alfabank.dfa.unleash.agent.unleashagent.starter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.alfabank.dfa.unleash.agent.UnleashAgent;
import ru.alfabank.dfa.unleash.agent.auth.UnleashSessionManager;
import ru.alfabank.dfa.unleash.agent.client.UnleashClient;
import ru.alfabank.dfa.unleash.agent.client.UnleashClientFactory;
import ru.alfabank.dfa.unleash.agent.common.UnleashSynchronizers;
import ru.alfabank.dfa.unleash.agent.common.SynchronizerFactory;
import ru.alfabank.dfa.unleash.agent.configuration.UnleashConfiguration;
import ru.alfabank.dfa.unleash.agent.configuration.YamlConfigurationParser;

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
    SynchronizerFactory unleashUpdatersFactory(UnleashClient unleashClient,
                                               UnleashSessionManager unleashSessionManager) {
        return new SynchronizerFactory(unleashClient, unleashSessionManager);
    }

    @Bean
    UnleashSynchronizers unleashUpdaters(SynchronizerFactory synchronizerFactory) {
        return synchronizerFactory.buildUpdaters();
    }


    @Bean
    UnleashAgent unleashAgent(UnleashSynchronizers unleashSynchronizers) {
        return new UnleashAgent(unleashSynchronizers);
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
