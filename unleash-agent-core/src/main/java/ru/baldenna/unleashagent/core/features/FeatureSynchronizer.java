package ru.baldenna.unleashagent.core.features;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.configuration.UnleashProjectConfiguration;
import ru.baldenna.unleashagent.core.features.model.CompareResult;
import ru.baldenna.unleashagent.core.features.model.CompareResultType;
import ru.baldenna.unleashagent.core.features.model.CreateFeatureDto;
import ru.baldenna.unleashagent.core.features.model.Feature;
import ru.baldenna.unleashagent.core.features.model.UpdateFeatureDto;

import java.util.ArrayList;
import java.util.List;

import static ru.baldenna.unleashagent.core.features.model.CompareResultType.CHANGED;
import static ru.baldenna.unleashagent.core.features.model.CompareResultType.EQUAL;
import static ru.baldenna.unleashagent.core.features.model.CompareResultType.ERROR;
import static ru.baldenna.unleashagent.core.features.model.CompareResultType.NOT_EQUAL;

/**
 * Synchronizes features in Unleash with features in the given configuration
 */
@Slf4j
@RequiredArgsConstructor
public class FeatureSynchronizer {

    final UnleashClient unleashClient;
    final UnleashSessionManager sessionManager;

    public boolean synchronize(String projectName, UnleashProjectConfiguration newConfiguration) {
        try {
            var success = true;
            log.info("Check unleash features for update");
            var remoteFeatures = getRemoteFeatures(projectName);
            var localFeatures = newConfiguration.features();

            var featuresToCreate = new ArrayList<Feature>();
            var featuresToUpdate = new ArrayList<Feature>();
            var featuresToDelete = new ArrayList<Feature>();

            for (Feature localFlag : localFeatures) {
                var featureAlreadyActual = remoteFeatures.stream()
                        .map((remoteFeature) -> compareFeatures(localFlag, remoteFeature))
                        .anyMatch(compareResult -> compareResult.type() == EQUAL);
                if (featureAlreadyActual) {
                    log.debug("Feature {} already exists and actual", localFlag.name());
                    continue;
                }

                var featureChanged = remoteFeatures.stream()
                        .map((remoteFeature) -> compareFeatures(localFlag, remoteFeature))
                        .filter(compareResult -> compareResult.type() == CHANGED)
                        .findFirst();
                if (featureChanged.isPresent()) {
                    log.info("Feature {} exists but need to be changed. Reason: {}",
                            localFlag.name(), featureChanged.get().details());
                    featuresToUpdate.add(localFlag);
                    continue;
                }

                log.info("Feature {} not found in Unleash and need to be created", localFlag.name());
                featuresToCreate.add(localFlag);
            }
            for (Feature remoteFlag : remoteFeatures) {
                if (localFeatures.stream().noneMatch(localFlag -> localFlag.name().equals(remoteFlag.name()))) {
                    log.info("Feature {} exists in Unleash but not declared in local config. Feature will be deleted",
                            remoteFlag.name());
                    featuresToDelete.add(remoteFlag);
                }
            }

            if (featuresToCreate.size() + featuresToUpdate.size() + featuresToDelete.size() != 0) {
                log.info("Features was compared. Count to create = {}, count to update = {}, count to delete = {}",
                        featuresToCreate.size(), featuresToUpdate.size(), featuresToDelete.size());
            } else {
                log.info("Unleash features already up to date");
            }

            success = featuresToCreate.stream().allMatch(feature -> createFeature(feature, projectName));
            success = success && featuresToUpdate.stream().allMatch(feature -> updateFeature(feature, projectName));
            success = success && featuresToDelete.stream().allMatch(feature -> deleteFeature(feature, projectName));

            return success;
        } catch (Exception e) {
            log.warn("Error while feature synchronization in project {}", projectName);
            log.debug(e.getMessage(), e);

            return false;
        }
    }

    private List<Feature> getRemoteFeatures(String projectName) {
        return unleashClient.getFeatures(projectName, sessionManager.getSessionCookie()).features();
    }

    private CompareResult compareFeatures(Feature local, Feature remote) {
        try {
            boolean nameEquals = local.name().equals(remote.name());
            if (!nameEquals) {
                String details = "Different features: " + local.name() + " and " + remote.name();
                return new CompareResult(NOT_EQUAL, details);
            }
            boolean typeEquals = local.type().equals(remote.type());
            if (!typeEquals) {
                String details = String.format("Feature %s type changed: %s -> %s",
                        remote.name(), remote.type(), local.type());

                return new CompareResult(CompareResultType.CHANGED, details);
            }
            boolean descriptionEquals = local.description().equals(remote.description());
            if (!descriptionEquals) {
                String details = String.format("Feature %s description changed: %s -> %s",
                        remote.name(), remote.description(), local.description());

                return new CompareResult(CompareResultType.CHANGED, details);
            }
            return new CompareResult(CompareResultType.EQUAL, "Feature " + remote.name() + " has actual state");
        } catch (Exception e) {
            log.error("Error trying compare features", e);
            return new CompareResult(ERROR, e.getMessage());
        }
    }

    private boolean createFeature(Feature feature, String project) {
        try {
            CreateFeatureDto createFeatureDto = new CreateFeatureDto(
                    feature.name(),
                    feature.type(),
                    feature.description(),
                    feature.tags());

            unleashClient.createFeature(project, createFeatureDto, sessionManager.getSessionCookie());

            log.info("Feature created: {}", createFeatureDto.name());

            return true;
        } catch (Exception e) {
            log.warn("Error creating feature {} in project {}", feature.name(), project);
            log.debug(e.getMessage(), e);

            return false;
        }
    }

    private boolean updateFeature(Feature feature, String project) {
        try {
            unleashClient.updateFeature(project, feature.name(), new UpdateFeatureDto(
                    feature.type(),
                    feature.description()), sessionManager.getSessionCookie());
            log.info("Feature updated: {}", feature.name());

            return true;
        } catch (Exception e) {
            log.warn("Error updating feature {} in project {}", feature.name(), project);
            log.debug(e.getMessage(), e);

            return false;
        }
    }

    private boolean deleteFeature(Feature feature, String project) {
        try {
            unleashClient.archiveFeature(project, feature.name(), sessionManager.getSessionCookie());
            unleashClient.deleteFeature(feature.name(), sessionManager.getSessionCookie());

            log.info("Feature deleted: {}", feature.name());
            return true;
        } catch (Exception e) {
            log.warn("Error deleting feature {} in project {}", feature.name(), project);
            log.debug(e.getMessage(), e);
            return false;
        }
    }
}
