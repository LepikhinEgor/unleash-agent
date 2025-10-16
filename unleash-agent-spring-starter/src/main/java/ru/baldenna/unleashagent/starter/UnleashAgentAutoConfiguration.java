package ru.baldenna.unleashagent.starter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.baldenna.unleashagent.core.UnleashAgent;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.client.UnleashClientFactory;
import ru.baldenna.unleashagent.core.common.UnleashConfigCollector;
import ru.baldenna.unleashagent.core.common.UnleashSynchronizers;
import ru.baldenna.unleashagent.core.common.SynchronizerFactory;
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
    SynchronizerFactory unleashUpdatersFactory(UnleashClient unleashClient,
                                               UnleashSessionManager unleashSessionManager) {
        return new SynchronizerFactory(unleashClient, unleashSessionManager);
    }

    @Bean
    UnleashSynchronizers unleashUpdaters(SynchronizerFactory synchronizerFactory) {
        return synchronizerFactory.buildUpdaters();
    }

    @Bean
    UnleashConfigCollector unleashUpdaters(UnleashClient unleashClient,
                                           UnleashSessionManager unleashSessionManager,
                                           YamlConfigurationParser yamlConfigurationParser) {
        return new UnleashConfigCollector(unleashClient, unleashSessionManager, yamlConfigurationParser);
    }

    @Bean
    UnleashAgent unleashAgent(UnleashSynchronizers unleashSynchronizers, UnleashConfigCollector configCollector) {
        return new UnleashAgent(unleashSynchronizers, configCollector);
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
