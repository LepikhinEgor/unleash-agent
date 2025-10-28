package ru.alfabank.dfa.unleash.agent;

import lombok.RequiredArgsConstructor;
import ru.alfabank.dfa.unleash.agent.common.UnleashSynchronizers;
import ru.alfabank.dfa.unleash.agent.configuration.UnleashConfiguration;

@RequiredArgsConstructor
public class UnleashAgent {

    private final UnleashSynchronizers updaters;

    public boolean synchronizeConfiguration(UnleashConfiguration unleashConfiguration) {
        var success = true;
        success = success && updaters.tagSynchronizer().synchronize(unleashConfiguration);
        success = success && updaters.segmentSynchronizer().synchronize(unleashConfiguration);
        success = success && updaters.contextFieldSynchronizer().synchronize(unleashConfiguration);
        success = success && updaters.apiTokenSynchronizer().synchronize(unleashConfiguration);

        for (var project : unleashConfiguration.projects().entrySet()) {
            var projectName = project.getKey();
            var projectConfiguration = project.getValue();

            success = success && updaters.featureSynchronizer().synchronize(projectName, projectConfiguration);
            success = success && updaters.featureTagUpdater().synchronize(projectName, projectConfiguration);

            for (var environment: projectConfiguration.environments()) {
                success = success && updaters.strategySynchronizer().synchronize(projectName, environment);
            }
        }

        return success;
    }

}
