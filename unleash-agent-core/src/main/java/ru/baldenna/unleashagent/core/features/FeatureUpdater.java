package ru.baldenna.unleashagent.core.features;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.features.model.CompareResult;
import ru.baldenna.unleashagent.core.features.model.CompareResultType;
import ru.baldenna.unleashagent.core.features.model.CreateFeatureDto;
import ru.baldenna.unleashagent.core.features.model.Feature;
import ru.baldenna.unleashagent.core.features.model.UpdateFeatureDto;

import java.util.ArrayList;

import static ru.baldenna.unleashagent.core.features.model.CompareResultType.CHANGED;
import static ru.baldenna.unleashagent.core.features.model.CompareResultType.EQUAL;
import static ru.baldenna.unleashagent.core.features.model.CompareResultType.ERROR;
import static ru.baldenna.unleashagent.core.features.model.CompareResultType.NOT_EQUAL;

@Slf4j
@RequiredArgsConstructor
public class FeatureUpdater {

    private static final String PROJECT_NAME = "default";

    final UnleashClient unleashClient;
    final UnleashSessionManager unleashSessionManager;

    public void update(UnleashConfiguration newConfiguration) {
        log.info("Check unleash features for update");
        var remoteFeatures = unleashClient.getFeatures(10000, "IS:default", unleashSessionManager.getUnleashSessionCookie()).features();
        var localFeatures = newConfiguration.features();

        var flagsToCreate = new ArrayList<Feature>();
        var flagsToUpdate = new ArrayList<Feature>();
        var flagsToDelete = new ArrayList<Feature>();

        for (Feature localFlag : localFeatures) {
            var featureAlreadyActual = remoteFeatures.stream().
                    map((remoteFeature) -> compareFeatures(localFlag,remoteFeature) )
                    .anyMatch(compareResult -> compareResult.type() == EQUAL);
            if (featureAlreadyActual) {
                log.debug("Feature {} already exists and actual", localFlag.name());
                continue;
            }

            var featureChanged = remoteFeatures.stream().
                    map((remoteFeature) -> compareFeatures(localFlag,remoteFeature) )
                    .filter(compareResult -> compareResult.type() == CHANGED)
                    .findFirst();
            if (featureChanged.isPresent()) {
                log.info("Feature {} exists but need to be changed. Reason: {}", localFlag.name(), featureChanged.get().details());
                flagsToUpdate.add(localFlag);
                continue;
            }

            log.info("Feature {} not found in Unleash and need to be created", localFlag.name());
            flagsToCreate.add(localFlag);
        }
        for (Feature remoteFlag : remoteFeatures) {
            if (localFeatures.stream().noneMatch(localFlag -> localFlag.name().equals(remoteFlag.name()))) {
                log.info("Feature {} exists in Unleash but not declared in local config. Feature will be deleted", remoteFlag.name());
                flagsToDelete.add(remoteFlag);
            }
        }

        if (flagsToCreate.size() + flagsToUpdate.size() + flagsToDelete.size() != 0) {
            log.info("Feature states was compared. Count to create = {}, count to update = {}, count to delete = {}", flagsToCreate.size(), flagsToUpdate.size(), flagsToDelete.size());
        } else {
            log.info("Unleash features already up to date");
        }

        flagsToCreate.forEach(this::createFeature);
        flagsToUpdate.forEach(this::updateFeature);
        flagsToDelete.forEach(this::deleteFeature);
    }

    private CompareResult compareFeatures(Feature local, Feature remote) {
        try {
            boolean nameEquals = local.name().equals(remote.name());
            if (!nameEquals) {
                return new CompareResult(NOT_EQUAL, "Different features: " + local.name() + " and " + remote.name());
            }
            boolean typeEquals = local.type().equals(remote.type());
            if (!typeEquals) {
                return new CompareResult(CompareResultType.CHANGED, "Feature " + remote.name() + " type changed: " + remote.type() + " -> " + local.type());
            }
            boolean descriptionEquals = local.description().equals(remote.description());
            if (!descriptionEquals) {
                return new CompareResult(CompareResultType.CHANGED, "Feature " + remote.name() + " description changed: " + remote.description() + " -> " + local.description());
            }
            return new CompareResult(CompareResultType.EQUAL, "Feature " + remote.name() + " has actual state");
        } catch (Exception e) {
            log.error("Error trying compare features", e);
            return new CompareResult(ERROR, e.getMessage());
        }
    }

    public void createFeature(Feature createFeatureTask) {
        CreateFeatureDto createFeatureDto = new CreateFeatureDto(
                createFeatureTask.name(),
                createFeatureTask.type(),
                createFeatureTask.description(),
                createFeatureTask.tags());

        unleashClient.createFeature(PROJECT_NAME, createFeatureDto, unleashSessionManager.getUnleashSessionCookie());

        log.info("Feature created: {}", createFeatureDto.getName());
    }

    public void updateFeature(Feature updateFeatureTask) {
        unleashClient.updateFeature(PROJECT_NAME, updateFeatureTask.name(), new UpdateFeatureDto(
                updateFeatureTask.type(),
                updateFeatureTask.description()), unleashSessionManager.getUnleashSessionCookie());
        log.info("Feature updated: {}", updateFeatureTask.name());
    }

    public void deleteFeature(Feature deleteFeatureTask) {
        unleashClient.archiveFeature(PROJECT_NAME, deleteFeatureTask.name(), unleashSessionManager.getUnleashSessionCookie());
        unleashClient.deleteFeature(deleteFeatureTask.name(), unleashSessionManager.getUnleashSessionCookie());

        log.info("Feature deleted: {}", deleteFeatureTask.name());
    }
}
