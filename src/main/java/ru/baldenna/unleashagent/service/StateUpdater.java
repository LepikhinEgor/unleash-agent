package ru.baldenna.unleashagent.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.baldenna.unleashagent.client.UnleashClient;
import ru.baldenna.unleashagent.dto.CreateFeatureDto;
import ru.baldenna.unleashagent.dto.UpdateFeatureDto;
import ru.baldenna.unleashagent.task.CreateFeatureTask;
import ru.baldenna.unleashagent.task.DeleteFeatureTask;
import ru.baldenna.unleashagent.task.UpdateFeatureTask;

@Slf4j
@Service
@AllArgsConstructor
public class StateUpdater {

    private static final String PROJECT_NAME = "default";

    UnleashClient unleashClient;
    UnleashSessionManager unleashSessionManager;

    public void createFeature(CreateFeatureTask createFeatureTask) {
        CreateFeatureDto createFeatureDto = new CreateFeatureDto(
                createFeatureTask.name(),
                "release",
                createFeatureTask.description(),
                createFeatureTask.tags());

        unleashClient.createFeature(PROJECT_NAME, createFeatureDto, unleashSessionManager.getUnleashSessionCookie());

        log.info("Feature created: {}", createFeatureDto.getName());
    }

    public void updateFeature(UpdateFeatureTask updateFeatureTask) {
        unleashClient.updateFeature(PROJECT_NAME, updateFeatureTask.name(), new UpdateFeatureDto(
                updateFeatureTask.type(),
                updateFeatureTask.description()), unleashSessionManager.getUnleashSessionCookie());
        log.info("Feature updated: {}", updateFeatureTask.name());
    }

    public void deleteFeature(DeleteFeatureTask deleteFeatureTask) {
        unleashClient.archiveFeature(PROJECT_NAME, deleteFeatureTask.name(), unleashSessionManager.getUnleashSessionCookie());

        log.info("Feature deleted: {}", deleteFeatureTask.name());
    }
}
