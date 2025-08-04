package ru.baldenna.unleashagent.core;

import lombok.AllArgsConstructor;
import ru.baldenna.unleashagent.core.common.UnleashUpdaters;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;

@AllArgsConstructor
public class UnleashAgent {

    UnleashUpdaters updaters;

    public void synchronizeConfiguration(UnleashConfiguration unleashConfiguration){
        updaters.tagUpdater().update(unleashConfiguration);
        updaters.featureUpdater().update(unleashConfiguration);
        updaters.featureTagUpdater().update(unleashConfiguration);
    }

}
