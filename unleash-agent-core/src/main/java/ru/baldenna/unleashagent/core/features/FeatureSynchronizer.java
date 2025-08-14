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

@Slf4j
@RequiredArgsConstructor
public class FeatureSynchronizer {

    final UnleashClient unleashClient;
    final UnleashSessionManager sessionManager;

    public void synchronize(String projectName, UnleashProjectConfiguration newConfiguration) {
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
            log.info("Feature states was compared. Count to create = {}, count to update = {}, count to delete = {}",
                    featuresToCreate.size(), featuresToUpdate.size(), featuresToDelete.size());
        } else {
            log.info("Unleash features already up to date");
        }

        featuresToCreate.forEach(feature -> createFeature(feature, projectName));
        featuresToUpdate.forEach(feature -> updateFeature(feature, projectName));
        featuresToDelete.forEach(feature -> deleteFeature(feature, projectName));
    }

    private List<Feature> getRemoteFeatures(String projectName) {
        return unleashClient.getFeatures(projectName, sessionManager.parseUnleashSession()).features();
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

    private void createFeature(Feature createFeatureTask, String project) {
        CreateFeatureDto createFeatureDto = new CreateFeatureDto(
                createFeatureTask.name(),
                createFeatureTask.type(),
                createFeatureTask.description(),
                createFeatureTask.tags());

        unleashClient.createFeature(project, createFeatureDto, sessionManager.parseUnleashSession());

        log.info("Feature created: {}", createFeatureDto.name());
    }

    private void updateFeature(Feature updateFeatureTask, String project) {
        unleashClient.updateFeature(project, updateFeatureTask.name(), new UpdateFeatureDto(
                updateFeatureTask.type(),
                updateFeatureTask.description()), sessionManager.parseUnleashSession());
        log.info("Feature updated: {}", updateFeatureTask.name());
    }

    private void deleteFeature(Feature deleteFeatureTask, String project) {
        unleashClient.archiveFeature(project, deleteFeatureTask.name(), sessionManager.parseUnleashSession());
        unleashClient.deleteFeature(deleteFeatureTask.name(), sessionManager.parseUnleashSession());

        log.info("Feature deleted: {}", deleteFeatureTask.name());
    }
}
