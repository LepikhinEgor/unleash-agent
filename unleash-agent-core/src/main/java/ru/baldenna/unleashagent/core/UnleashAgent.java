package ru.baldenna.unleashagent.core;

import lombok.RequiredArgsConstructor;
import ru.baldenna.unleashagent.core.common.UnleashUpdaters;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;

@RequiredArgsConstructor
public class UnleashAgent {

    private final UnleashUpdaters updaters;

    public void synchronizeConfiguration(UnleashConfiguration unleashConfiguration) {
        updaters.tagUpdater().update(unleashConfiguration);

        unleashConfiguration.projects().forEach((projectName, projectConfiguration) -> {
            updaters.featureUpdater().update(projectName, projectConfiguration);
            updaters.featureTagUpdater().update(projectName, projectConfiguration);
        });
    }

}
