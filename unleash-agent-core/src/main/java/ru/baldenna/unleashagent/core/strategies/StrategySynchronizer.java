package ru.baldenna.unleashagent.core.strategies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.projects.ProjectEnvironment;
import ru.baldenna.unleashagent.core.strategies.model.Strategy;

import java.util.ArrayList;

import static ru.baldenna.unleashagent.core.utils.CompareUtils.notEquals;

/**
 * TODO fix update segment by name
 * TODO fix bug when unleash return incorrect variant auto-weight for 33%
 * TODO fix synch strategies with same name, NEED RESEARCH. Strategy name not unique
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
        var strategyEquals = true;

        // Compare name
        if (notEquals(localStrategy.name(), existingStrategy.name())) {
            log.info("Strategies differ in 'name' field: local={}, remote={}",
                    localStrategy.name(), existingStrategy.name());
            strategyEquals = false;
        }

        // Compare title
        if (notEquals(localStrategy.title(), existingStrategy.title())) {
            log.info("Strategies differ in 'title' field: local={}, remote={}",
                    localStrategy.title(), existingStrategy.title());
            strategyEquals = false;
        }

        // Compare disabled
        if (notEquals(localStrategy.disabled(), existingStrategy.disabled())) {
            log.info("Strategies differ in 'disabled' field: local={}, remote={}",
                    localStrategy.disabled(), existingStrategy.disabled());
            strategyEquals = false;
        }

        // Compare sortOrder
        if (notEquals(localStrategy.sortOrder(), existingStrategy.sortOrder())) {
            log.info("Strategies differ in 'sortOrder' field: local={}, remote={}",
                    localStrategy.sortOrder(), existingStrategy.sortOrder());
            strategyEquals = false;
        }

        // Compare constraints
        strategyEquals = strategyEquals && isConstraintsEquals(localStrategy, existingStrategy, feature);

        // Compare variants
        strategyEquals = strategyEquals && isVariantEquals(localStrategy, existingStrategy, feature);

        // Compare parameters
        strategyEquals = strategyEquals && isParametersEquals(localStrategy, existingStrategy, feature);

        // Compare segments
        strategyEquals = strategyEquals && isSegmentsEquals(localStrategy, existingStrategy, feature);

        return strategyEquals;
    }

    private boolean isParametersEquals(Strategy localStrategy, Strategy existingStrategy, String feature) {
        var parametersEquals = true;
        var localParameters = localStrategy.parameters();
        var remoteParameters = existingStrategy.parameters();
        if (localParameters.size() != remoteParameters.size()) {
            log.info("Strategies in feature {} differ in 'parameters' count: local={}, remote={}",
                    feature, localParameters.size(), remoteParameters.size());
            parametersEquals = false;
        }
        for (var entry : localParameters.entrySet()) {
            var localValue = entry.getValue();
            var remoteValue = remoteParameters.get(entry.getKey());
            if (notEquals(localValue, remoteValue)) {
                log.info("Strategies in feature {} differ in 'parameters' for key {}: local={}, remote={}",
                        feature, entry.getKey(), localValue, remoteValue);
                parametersEquals = false;
            }
        }
        return parametersEquals;
    }

    private boolean isVariantEquals(Strategy localStrategy, Strategy existingStrategy, String feature) {
        var variantEquals = true;
        var localVariants = localStrategy.variants();
        var remoteVariants = existingStrategy.variants();
        if (localVariants.size() != remoteVariants.size()) {
            log.info("Strategies in feature {} differ in 'variants' count: local={}, remote={}",
                    feature, localVariants.size(), remoteVariants.size());
            variantEquals = false;
        }
        for (int i = 0; i < localVariants.size(); i++) {
            var localVariant = localVariants.get(i);
            var remoteVariant = remoteVariants.get(i);
            if (notEquals(localVariant, remoteVariant)) {
                log.info("Strategies in feature {} differ in 'variants' at index {}: local={}, remote={}",
                        feature, i, localVariant, remoteVariant);
                variantEquals = false;
            }
        }
        return variantEquals;
    }

    private boolean isSegmentsEquals(Strategy localStrategy, Strategy existingStrategy, String feature) {
        var segmentEquals = true;
        var localSegments = localStrategy.segments();
        var remoteSegments = existingStrategy.segments();
        if (localSegments.size() != remoteSegments.size()) {
            log.info("Strategies in feature {} differ in 'segments' count: local={}, remote={}",
                    feature, localSegments.size(), remoteSegments.size());
            segmentEquals = false;
        }
        for (int i = 0; i < localSegments.size(); i++) {
            var localSegment = localSegments.get(i);
            var remoteSegment = remoteSegments.get(i);
            if (notEquals(localSegment, remoteSegment)) {
                log.info("Strategies in feature {} differ in 'segments' at index {}: local={}, remote={}",
                        feature, i, localSegment, remoteSegment);
                segmentEquals = false;
            }
        }
        return segmentEquals;
    }

    private boolean isConstraintsEquals(Strategy localStrategy, Strategy existingStrategy, String feature) {
        var constraintsEquals = true;
        var localConstraints = localStrategy.constraints();
        var remoteConstraints = existingStrategy.constraints();
        if (localConstraints.size() != remoteConstraints.size()) {
            log.info("Strategies in feature {} differ in 'constraints' count: local={}, remote={}",
                    feature, localConstraints.size(), remoteConstraints.size());
            constraintsEquals = false;
        }
        for (int i = 0; i < localConstraints.size(); i++) {
            var localConstraint = localConstraints.get(i);
            if (remoteConstraints.size() <= i) {
                log.info("Strategies in feature {} differ in 'constraints' at index {}: local={}, remote=missed",
                        feature, i, localConstraint);
                return false;
            }
            var remoteConstraint = remoteConstraints.get(i);
            if (notEquals(localConstraint, remoteConstraint)) {
                log.info("Strategies in feature {} differ in 'constraints' at index {}: local={}, remote={}",
                        feature, i, localConstraint, remoteConstraint);
                constraintsEquals = false;
            }
        }
        return constraintsEquals;
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
