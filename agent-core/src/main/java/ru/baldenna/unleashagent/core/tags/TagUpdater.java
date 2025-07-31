package ru.baldenna.unleashagent.core.tags;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.baldenna.unleashagent.client.UnleashClient;
import ru.baldenna.unleashagent.common.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.common.config.UnleashConfig;
import ru.baldenna.unleashagent.tags.model.Tag;

import java.util.ArrayList;

@Slf4j
@Service
@AllArgsConstructor
public class TagUpdater {

    UnleashConfig unleashConfig;
    UnleashClient unleashClient;
    UnleashSessionManager unleashSessionManager;

    public void update() {
        log.info("Check unleash tags for update");
        var remoteTags = unleashClient.getTags(unleashSessionManager.getUnleashSessionCookie())
                .getBody().tags();
        var localTags = unleashConfig.tags();

        var tagsToCreate = new ArrayList<Tag>();
        var tagsToDelete = new ArrayList<Tag>();

        for (Tag localTag : localTags) {
            var tagAlreadyExists = remoteTags.stream()
                    .anyMatch(remoteTag -> remoteTag.equals(localTag));
            if (tagAlreadyExists) {
                log.debug("Tag {}  with type {} already exists", localTag.value(), localTag.type());
            } else {
                log.info("Tag {} with type {} not found in Unleash and need to be created", localTag.value(), localTag.type());
                tagsToCreate.add(localTag);
            }
        }

        for (Tag remoteTag : remoteTags) {
            if (localTags.stream().noneMatch(localTag -> localTag.equals(remoteTag))) {
                log.info("Feature {} with type {} exists in Unleash but not declared in local config. Feature will be deleted", remoteTag.value(), remoteTag.type());
                tagsToDelete.add(remoteTag);
            }
        }

        if (tagsToCreate.size() + tagsToDelete.size() != 0) {
            log.info("Tag states was compared. Count to create = {}, count to update = {}, count to delete = {}", tagsToCreate.size(), 0, tagsToDelete);
        } else {
            log.info("Unleash tags already up to date");
        }

        tagsToCreate.forEach(this::createTag);
        tagsToDelete.forEach(this::deleteTag);
    }

    public void createTag(Tag createTagTask) {
        unleashClient.createTag(new Tag(createTagTask.value(), createTagTask.type()), unleashSessionManager.getUnleashSessionCookie());

        log.info("Tag created: {}:{}", createTagTask.type(), createTagTask.value());
    }

    public void deleteTag(Tag deleteTagTask) {
        unleashClient.deleteTag(deleteTagTask.value(), deleteTagTask.type(), unleashSessionManager.getUnleashSessionCookie());

        log.info("Tag deleted: {}:{}", deleteTagTask.type(), deleteTagTask.value());
    }
}
