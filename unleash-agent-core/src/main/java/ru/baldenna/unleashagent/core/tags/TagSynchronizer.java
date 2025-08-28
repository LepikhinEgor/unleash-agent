package ru.baldenna.unleashagent.core.tags;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.tags.model.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

/**
 * Synchronizes tags in Unleash with tags in the given configuration
 */
@Slf4j
@RequiredArgsConstructor
public class TagSynchronizer {

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    public void synchronize(UnleashConfiguration newConfiguration) {
        try {
            log.info("Check unleash tags for update");
            var remoteTags = Optional.ofNullable(unleashClient.getTags(unleashSessionManager.getSessionCookie())
                    .tags()).orElse(Collections.emptyList());
            var localTags = newConfiguration.tags();

            var tagsToCreate = new ArrayList<Tag>();
            var tagsToDelete = new ArrayList<Tag>();

            for (Tag localTag : localTags) {
                var tagAlreadyExists = remoteTags.stream()
                        .anyMatch(remoteTag -> remoteTag.equals(localTag));
                if (tagAlreadyExists) {
                    log.debug("Tag {}  with type {} already exists", localTag.value(), localTag.type());
                } else {
                    log.info("Tag {} with type {} not found in Unleash and need to be created",
                            localTag.value(), localTag.type());
                    tagsToCreate.add(localTag);
                }
            }

            for (Tag remoteTag : remoteTags) {
                if (localTags.stream().noneMatch(localTag -> localTag.equals(remoteTag))) {
                    log.info("Feature {} with type {} exists in Unleash but not declared in local config." +
                            " Feature will be deleted", remoteTag.value(), remoteTag.type());
                    tagsToDelete.add(remoteTag);
                }
            }

            if (tagsToCreate.size() + tagsToDelete.size() != 0) {
                log.info("Tag states was compared. Count to create = {}, count to update = {}, count to delete = {}",
                        tagsToCreate.size(), 0, tagsToDelete);
            } else {
                log.info("Unleash tags already up to date");
            }

            tagsToCreate.forEach(this::createTag);
            tagsToDelete.forEach(this::deleteTag);
        } catch (Exception e) {
            log.warn("Error while tags synchronization");
            log.debug(e.getMessage(), e);
        }
    }

    private void createTag(Tag tag) {
        try {
            unleashClient.createTag(new Tag(tag.value(), tag.type()), unleashSessionManager.getSessionCookie());

            log.info("Tag created: {}:{}", tag.type(), tag.value());
        } catch (Exception e) {
            log.warn("Error creating tag {}:{}", tag.type(), tag.value());
            log.debug(e.getMessage(), e);
        }
    }

    private void deleteTag(Tag tag) {
        try {
            unleashClient.deleteTag(tag.type(), tag.value(), unleashSessionManager.getSessionCookie());

            log.info("Tag deleted: {}:{}", tag.type(), tag.value());
        } catch (Exception e) {
            log.warn("Error removing tag {}:{}", tag.type(), tag.value());
            log.debug(e.getMessage(), e);
        }
    }
}
