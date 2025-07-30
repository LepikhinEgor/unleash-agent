package ru.baldenna.unleashagent.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.baldenna.unleashagent.client.UnleashClient;
import ru.baldenna.unleashagent.dto.CreateFeatureDto;
import ru.baldenna.unleashagent.dto.Tag;
import ru.baldenna.unleashagent.dto.UpdateFeatureDto;
import ru.baldenna.unleashagent.task.CreateFeatureTask;
import ru.baldenna.unleashagent.task.CreateTagTask;
import ru.baldenna.unleashagent.task.DeleteFeatureTask;
import ru.baldenna.unleashagent.task.DeleteTagTask;
import ru.baldenna.unleashagent.task.UpdateFeatureTask;

@Slf4j
@Service
@AllArgsConstructor
public class UnleashStateUpdater {

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

    public void createTag(CreateTagTask createTagTask) {
        unleashClient.createTag(new Tag(createTagTask.value(), createTagTask.type()), unleashSessionManager.getUnleashSessionCookie());

        log.info("Tag created: {}:{}", createTagTask.type(), createTagTask.value());
    }

    public void deleteTag(DeleteTagTask deleteTagTask) {
        unleashClient.deleteTag(deleteTagTask.value(), deleteTagTask.type(), unleashSessionManager.getUnleashSessionCookie());

        log.info("Tag deleted: {}:{}", deleteTagTask.type(), deleteTagTask.value());
    }
}
