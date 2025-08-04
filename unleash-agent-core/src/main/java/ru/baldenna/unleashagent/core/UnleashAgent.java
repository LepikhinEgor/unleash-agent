package ru.baldenna.unleashagent.core;

import lombok.AllArgsConstructor;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.features.FeatureUpdater;
import ru.baldenna.unleashagent.core.featuretags.FeatureTagsUpdater;
import ru.baldenna.unleashagent.core.tags.TagUpdater;

@AllArgsConstructor
public class UnleashAgent {

    private TagUpdater tagUpdater;
    private FeatureUpdater  featureUpdater ;
    private FeatureTagsUpdater featureTagUpdater;

    public void updateConfiguration(){
        tagUpdater.update();
        featureUpdater.update();
        featureTagUpdater.update();
    }

}
