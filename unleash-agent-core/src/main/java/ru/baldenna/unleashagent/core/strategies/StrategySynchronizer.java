package ru.baldenna.unleashagent.core.strategies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.projects.ProjectEnvironment;
import ru.baldenna.unleashagent.core.strategies.model.Strategy;

import java.util.ArrayList;

import static ru.baldenna.unleashagent.core.utils.CompareUtils.deepCompare;


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
                        if (!isStrategyEquals(localStrategy, existingStrategy, feature)) {
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
                        log.info("Strategy {} in feature {} exists in Unleash but not declared in local config." +
                                " Strategy will be deleted", remoteStrategy.name(), feature);
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

        } catch (Exception e) {
            log.warn("Error while strategies synchronization", e);
            log.debug(e.getMessage(), e);

            return false;
        }

        return true;
    }

    private boolean isStrategyEquals(Strategy localStrategy, Strategy existingStrategy, String feature) {
        return deepCompare(localStrategy, existingStrategy);
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
        unleashClient.deleteFeatureStrategy(projectId,
                featureName,
                environment,
                strategyId, unleashSessionManager.getSessionCookie());
    }
}
