package ru.baldenna.unleashagent.core.contextfields;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.contextfields.model.ContextField;

import java.util.ArrayList;
import java.util.List;

import static ru.baldenna.unleashagent.core.utils.CompareUtils.notEquals;

@Slf4j
@RequiredArgsConstructor
public class ContextFieldSynchronizer {

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    private final List<String> defaultContextFields =
            List.of("appName", "currentTime", "environment", "sessionId", "userId");

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
        var contextFieldsEquals = true;
        if (notEquals(localContextField.name(), remoteContextField.name())) {
            log.info("Context fields differ in 'name' field: local={}, remote={}",
                    localContextField.name(), localContextField.name());
            return false;
        }
        if (notEquals(localContextField.description(), remoteContextField.description())) {
            log.info("Context fields differ in 'description' field: local={}, remote={}",
                    localContextField.description(), remoteContextField.description());
            contextFieldsEquals = false;
        }
        if (notEquals(localContextField.stickiness(), remoteContextField.stickiness())) {
            log.info("Context fields differ in 'stickiness' field: local={}, remote={}",
                    localContextField.stickiness(), remoteContextField.stickiness());
            contextFieldsEquals = false;
        }
        if (notEquals(localContextField.sortOrder(), remoteContextField.sortOrder())) {
            log.info("Context fields differ in 'sortOrder' field: local={}, remote={}",
                    localContextField.sortOrder(), remoteContextField.sortOrder());
            contextFieldsEquals = false;
        }

        var localLegalValues = localContextField.legalValues();
        var remoteLegalValues = remoteContextField.legalValues();
        if (localLegalValues.size() != remoteLegalValues.size()) {
            log.info("Context fields differ in 'legalValues' count: local={}, remote={}",
                    localLegalValues.size(), remoteLegalValues.size());
            return false;
        }

        for (int i = 0; i < localLegalValues.size(); i++) {
            var localLegalValue = localLegalValues.get(i);
            var remoteLegalValue = remoteLegalValues.get(i);
            if (notEquals(localLegalValue, remoteLegalValue)) {
                log.info("Context fields differ in 'legalValues' at index {}: local={}, remote={}",
                        i, localLegalValue, remoteLegalValue);
                contextFieldsEquals = false;
            }
        }

        return contextFieldsEquals;
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
