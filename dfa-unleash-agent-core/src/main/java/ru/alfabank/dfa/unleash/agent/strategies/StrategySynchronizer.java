package ru.alfabank.dfa.unleash.agent.strategies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.dfa.unleash.agent.auth.UnleashSessionManager;
import ru.alfabank.dfa.unleash.agent.client.UnleashClient;
import ru.alfabank.dfa.unleash.agent.features.model.Feature;
import ru.alfabank.dfa.unleash.agent.projects.ProjectEnvironment;
import ru.alfabank.dfa.unleash.agent.strategies.model.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.alfabank.dfa.unleash.agent.utils.CompareUtils.deepCompare;


/**
 * TODO fix update segment by name
 * TODO fix bug when unleash return incorrect variant auto-weight for 33%
 * TODO fix synch strategies with same name, NEED RESEARCH. Strategy name not unique
 * TODO kebab-case for configuration
 */
@Slf4j
@RequiredArgsConstructor
public class StrategySynchronizer {


    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    public boolean synchronize(String project, ProjectEnvironment environmentConfiguration) {
        try {
            log.info("Check unleash strategies for update");
            environmentConfiguration.featureStrategies().forEach((feature, featureStrategies) -> {
                var remoteStrategies = unleashClient.getFeatureStrategies(
                        project,
                        feature,
                        environmentConfiguration.name(),
                        unleashSessionManager.getSessionCookie()
                );

                var localStrategies = environmentConfiguration.featureStrategies().get(feature);

                var strategiesToCreate = new ArrayList<Strategy>();
                var strategiesToUpdate = new ArrayList<Strategy>();
                var strategiesToDelete = new ArrayList<Strategy>();

                for (Strategy localStrategy : localStrategies) {
                    var strategyAlreadyExists = remoteStrategies.stream()
                            .anyMatch(remoteStrategy -> remoteStrategy.name().equals(localStrategy.name()));
                    if (strategyAlreadyExists) {
                        var existingStrategy = remoteStrategies.stream()
                                .filter(remoteStrategy -> remoteStrategy.name().equals(localStrategy.name()))
                                .findFirst()
                                .orElseThrow();
                        if (!isStrategyEquals(localStrategy, existingStrategy)) {
                            log.info("Strategy {} with in feature {} needs to be updated",
                                    localStrategy.name(), feature);
                            strategiesToUpdate.add(localStrategy.copyWithId(existingStrategy.id()));
                        } else {
                            log.debug("Strategy {} in feature {} already exists and is up to date",
                                    localStrategy.name(), feature);
                        }
                    } else {
                        log.info("Strategy {} in feature {} not found in Unleash and needs to be created",
                                localStrategy.name(), feature);
                        strategiesToCreate.add(localStrategy);
                    }
                }

                for (Strategy remoteStrategy : remoteStrategies) {
                    if (localStrategies.stream().noneMatch(local -> local.name().equals(remoteStrategy.name()))) {
                        log.info("Strategy {} in feature {} exists in Unleash but not declared in local config."
                                + " Strategy will be deleted", remoteStrategy.name(), feature);
                        strategiesToDelete.add(remoteStrategy.copyWithId(remoteStrategy.id()));
                    }
                }

                if (strategiesToCreate.size() + strategiesToUpdate.size() + strategiesToDelete.size() != 0) {
                    log.info("Strategy states were compared. To create = {}, to update = {}, to delete = {}",
                            strategiesToCreate.size(), strategiesToUpdate.size(), strategiesToDelete.size());
                } else {
                    log.info("Unleash strategies are already up to date");
                }

                strategiesToCreate.forEach(strategy ->
                        addFeatureStrategy(project, feature, environmentConfiguration.name(), strategy)
                );
                strategiesToUpdate.forEach(strategy ->
                        updateStrategy(project, feature, environmentConfiguration.name(), strategy.id(), strategy)
                );
                strategiesToDelete.forEach(strategy ->
                        deleteStrategy(project, feature, environmentConfiguration.name(), strategy.id())
                );
            });
            var remoteStrategies = getUnleashFeatures(project).stream()
                    .map(feature -> Map.entry(feature.name(), getFeatureStrategies(project, environmentConfiguration, feature)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


            remoteStrategies.forEach((feature, strategies) -> {
                if (!environmentConfiguration.featureStrategies().containsKey(feature)) {
                    remoteStrategies.get(feature).forEach(localMissedStrategy ->
                            deleteStrategy(project, feature, environmentConfiguration.name(), localMissedStrategy.id()));
                }
            });

        } catch (Exception e) {
            log.warn("Error while strategies synchronization", e);
            log.debug(e.getMessage(), e);

            return false;
        }

        return true;
    }

    private List<Feature> getUnleashFeatures(String project) {
        return unleashClient.getFeatures(project, unleashSessionManager.getSessionCookie()).features();
    }

    private List<Strategy> getFeatureStrategies(String project, ProjectEnvironment environmentConfiguration, Feature feature) {
        return unleashClient.getFeatureStrategies(
                project,
                feature.name(),
                environmentConfiguration.name(),
                unleashSessionManager.getSessionCookie()
        );
    }

    private boolean isStrategyEquals(Strategy localStrategy, Strategy existingStrategy) {
        return deepCompare(localStrategy.copyWithId(existingStrategy.id()), existingStrategy);
    }

    private void addFeatureStrategy(String projectId, String featureName, String environment, Strategy strategy) {
        unleashClient.addFeatureStrategy(
                projectId,
                featureName,
                environment,
                strategy,
                unleashSessionManager.getSessionCookie()
        );
    }

    private void updateStrategy(String projectId,
                                String featureName,
                                String environment,
                                String strategyId,
                                Strategy strategy) {
        unleashClient.updateFeatureStrategy(
                projectId,
                featureName,
                environment,
                strategyId,
                strategy,
                unleashSessionManager.getSessionCookie());
    }

    private void deleteStrategy(String projectId, String featureName, String environment, String strategyId) {
        unleashClient.deleteFeatureStrategy(
                projectId,
                featureName,
                environment,
                strategyId,
                unleashSessionManager.getSessionCookie());
    }
}
