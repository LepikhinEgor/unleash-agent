package ru.baldenna.unleashagent.core.contextfields;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.common.UniversalComparator;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.contextfields.model.ContextField;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class ContextFieldSynchronizer {

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    private final List<String> defaultContextFields =
            List.of("appName", "currentTime", "environment", "sessionId", "userId");

    private final UniversalComparator universalComparator = new UniversalComparator();

    public boolean synchronize(UnleashConfiguration newConfiguration) {
        try {
            log.info("Check unleash context fields for update");
            var remoteContextFields = unleashClient.getContextFields(unleashSessionManager.getSessionCookie());
            var localContextFields = newConfiguration.contextFields();

            var contextFieldsToCreate = new ArrayList<ContextField>();
            var contextFieldsToUpdate = new ArrayList<ContextField>();
            var contextFieldsToDelete = new ArrayList<ContextField>();

            for (ContextField localContextField : localContextFields) {
                var contextFieldAlreadyExists = remoteContextFields.stream()
                        .anyMatch(remoteContextField -> remoteContextField.name().equals(localContextField.name()));
                if (contextFieldAlreadyExists) {
                    var existingContextField = remoteContextFields.stream()
                            .filter(remoteContextField -> remoteContextField.name().equals(localContextField.name()))
                            .findFirst()
                            .orElseThrow();
                    if (!isContextFieldsEquals(localContextField, existingContextField)) {
                        log.info("Context field {} with name {} needs to be updated",
                                localContextField.name(), localContextField.name());
                        contextFieldsToUpdate.add(localContextField);
                    } else {
                        log.debug("Context field {} with name {} already exists and is up to date",
                                localContextField.name(), localContextField.name());
                    }
                } else {
                    log.info("Context field {} with name {} not found in Unleash and needs to be created",
                            localContextField.name(), localContextField.name());
                    contextFieldsToCreate.add(localContextField);
                }
            }

            for (ContextField remoteContextField : remoteContextFields) {
                if (localContextFields.stream().noneMatch(local -> local.name().equals(remoteContextField.name()))) {
                    if (!defaultContextFields.contains(remoteContextField.name())) {
                        log.info("Context field {} with name {} exists in Unleash but not declared in local config." +
                                " Context field will be deleted", remoteContextField.name(), remoteContextField.name());
                        contextFieldsToDelete.add(remoteContextField);
                    } else {
                        log.debug("Default context field {} with name {} exists in Unleash " +
                                "but not declared in local config. Context field will not be deleted",
                                remoteContextField.name(), remoteContextField.name());
                    }
                }
            }

            if (contextFieldsToCreate.size() + contextFieldsToUpdate.size() + contextFieldsToDelete.size() != 0) {
                log.info("Context field states were compared. To create = {}, to update = {}, to delete = {}",
                        contextFieldsToCreate.size(), contextFieldsToUpdate.size(), contextFieldsToDelete.size());
            } else {
                log.info("Unleash context fields are already up to date");
            }

            contextFieldsToCreate.forEach(this::createContextField);
            contextFieldsToUpdate.forEach(this::updateContextField);
            contextFieldsToDelete.forEach(this::deleteContextField);
        } catch (Exception e) {
            log.warn("Error while context fields synchronization", e);
            log.debug(e.getMessage(), e);

            return false;
        }

        return true;
    }

    private boolean isContextFieldsEquals(ContextField localContextField, ContextField remoteContextField) {

       return universalComparator.compareWithLib(localContextField, remoteContextField);
    }

    private void createContextField(ContextField contextField) {
        try {
            unleashClient.createContextField(contextField, unleashSessionManager.getSessionCookie());
            log.info("Context field created: {}", contextField.name());
        } catch (Exception e) {
            log.warn("Error creating context field {}", contextField.name());
            log.debug(e.getMessage(), e);
        }
    }

    private void updateContextField(ContextField contextField) {
        try {
            unleashClient.updateContextField(
                    contextField.name(),
                    contextField,
                    unleashSessionManager.getSessionCookie()
            );
            log.info("Context field updated: {}", contextField.name());
        } catch (Exception e) {
            log.warn("Error updating context field {}", contextField.name());
            log.debug(e.getMessage(), e);
        }
    }

    private void deleteContextField(ContextField contextField) {
        try {
            unleashClient.deleteContextField(contextField.name(), unleashSessionManager.getSessionCookie());
            log.info("Context field deleted: {}", contextField.name());
        } catch (Exception e) {
            log.warn("Error deleting context field {}", contextField.name());
            log.debug(e.getMessage(), e);
        }
    }
}
