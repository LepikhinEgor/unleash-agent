package ru.baldenna.unleashagent.core.common;

import lombok.AllArgsConstructor;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.client.UnleashClientFactory;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.configuration.YamlConfigurationParser;
import ru.baldenna.unleashagent.core.features.FeatureUpdater;
import ru.baldenna.unleashagent.core.featuretags.FeatureTagsUpdater;
import ru.baldenna.unleashagent.core.tags.TagUpdater;

import java.nio.file.Files;
import java.nio.file.Path;

@AllArgsConstructor
public class UnleashUpdatersFactory {

    UnleashClient unleashClient;
    UnleashSessionManager unleashSessionManager;

    public UnleashUpdaters buildUpdaters() {

        var tagUpdater = new TagUpdater(unleashClient, unleashSessionManager);
        var featureUpdater = new FeatureUpdater(unleashClient, unleashSessionManager);
        var featureTagUpdater = new FeatureTagsUpdater(unleashClient, unleashSessionManager);

        return new UnleashUpdaters(tagUpdater, featureUpdater,featureTagUpdater);
    }
}
