package ru.baldenna.unleashagent.core.common;

import lombok.RequiredArgsConstructor;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.features.FeatureUpdater;
import ru.baldenna.unleashagent.core.featuretags.FeatureTagsUpdater;
import ru.baldenna.unleashagent.core.tags.TagUpdater;

@RequiredArgsConstructor
public class UnleashUpdatersFactory {

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    public UnleashUpdaters buildUpdaters() {

        var tagUpdater = new TagUpdater(unleashClient, unleashSessionManager);
        var featureUpdater = new FeatureUpdater(unleashClient, unleashSessionManager);
        var featureTagUpdater = new FeatureTagsUpdater(unleashClient, unleashSessionManager);

        return new UnleashUpdaters(tagUpdater, featureUpdater, featureTagUpdater);
    }
}
