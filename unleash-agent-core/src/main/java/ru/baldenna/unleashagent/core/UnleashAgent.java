package ru.baldenna.unleashagent.core;

import lombok.RequiredArgsConstructor;
import ru.baldenna.unleashagent.core.common.UnleashSynchronizers;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;

@RequiredArgsConstructor
public class UnleashAgent {

    private final UnleashSynchronizers updaters;

    public void synchronizeConfiguration(UnleashConfiguration unleashConfiguration) {
        updaters.tagSynchronizer().synchronize(unleashConfiguration);
        updaters.segmentSynchronizer().synchronize(unleashConfiguration);
        updaters.contextFieldSynchronizer().synchronize(unleashConfiguration);

        unleashConfiguration.projects().forEach((projectName, projectConfiguration) -> {
            updaters.featureSynchronizer().synchronize(projectName, projectConfiguration);
            updaters.featureTagUpdater().synchronize(projectName, projectConfiguration);

            projectConfiguration.environments().forEach(projectEnvironment ->
                    updaters.strategySynchronizer().synchronize(projectName, projectEnvironment)
            );
        });
    }

}
