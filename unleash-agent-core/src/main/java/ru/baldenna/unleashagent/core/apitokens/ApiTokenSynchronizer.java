package ru.baldenna.unleashagent.core.apitokens;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.apitokens.model.ApiToken;
import ru.baldenna.unleashagent.core.apitokens.model.CreateApiTokenRequest;
import ru.baldenna.unleashagent.core.apitokens.model.UpdateApiTokenRequest;

import java.time.ZonedDateTime;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class ApiTokenSynchronizer {

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    public boolean synchronize(UnleashConfiguration newConfiguration) {
        try {
            log.info("Check unleash api tokens for update");

            var session = unleashSessionManager.getSessionCookie();

            var remoteTokens = unleashClient.getApiTokens(session).tokens();

            // Build desired local tokens from configuration (projects/environments)
            var localTokens = newConfiguration.apiTokens();

            var tokensToCreate = new ArrayList<ApiToken>();
            var tokensToUpdate = new ArrayList<ApiToken>();
            var tokensToDelete = new ArrayList<ApiToken>();

            for (ApiToken local : localTokens) {
                var maybeRemote = remoteTokens.stream()
                        .filter(r -> isSameApiToken(local, r))
                        .findFirst();

                if (maybeRemote.isEmpty()) {
                    log.info("API secret {} not found in Unleash and needs to be created", local.tokenName());
                    tokensToCreate.add(local);
                } else {
                    var remote = maybeRemote.get();
                    if (!equals(ZonedDateTime.parse(local.expiresAt()), ZonedDateTime.parse(remote.expiresAt()))) {
                        log.info("API secret {} exists but expiresAt differs({}->{}) and needs update", local.tokenName(), local.expiresAt(), remote.expiresAt());
                        tokensToUpdate.add(local.withSecret(remote.secret()));
                    } else {
                        log.debug("API secret {} already exists and is up to date", local.tokenName());
                    }
                }
            }

            for (ApiToken remote : remoteTokens) {
                var existsLocally = localTokens.stream()
                        .anyMatch(local -> isSameApiToken(local, remote));
                if (!existsLocally) {
                    log.info("API secret {} exists in Unleash but not in local config. Will be deleted", remote.tokenName());
                    tokensToDelete.add(remote);
                }
            }

            if (tokensToCreate.size() + tokensToUpdate.size() + tokensToDelete.size() != 0) {
                log.info("API secret states were compared. To create = {}, to update = {}, to delete = {}",
                        tokensToCreate.size(), tokensToUpdate.size(), tokensToDelete.size());
            } else {
                log.info("Unleash API tokens are already up to date");
            }

            tokensToCreate.forEach(this::createToken);
            tokensToUpdate.forEach(this::updateToken);
            tokensToDelete.forEach(this::deleteToken);
        } catch (Exception e) {
            log.warn("Error while api tokens synchronization", e);
            log.debug(e.getMessage(), e);
            return false;
        }

        return true;
    }

    private boolean isSameApiToken(ApiToken local, ApiToken r) {
        return equals(local.tokenName(), r.tokenName())
                && equals(local.type(), r.type())
                && equals(local.project(), r.project())
                && equals(local.environment(), r.environment());
    }

    private boolean equals(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private void createToken(ApiToken token) {
        unleashClient.createApiToken(new CreateApiTokenRequest(
                token.expiresAt(),
                token.type(),
                token.environment(),
                token.project(),
                token.projects(),
                token.tokenName()
        ), unleashSessionManager.getSessionCookie());
    }

    private void updateToken(ApiToken token) {
        unleashClient.updateApiToken(token.secret(), new UpdateApiTokenRequest(
                token.expiresAt()
        ), unleashSessionManager.getSessionCookie());
    }

    private void deleteToken(ApiToken token) {
        unleashClient.deleteApiToken(token.secret(), unleashSessionManager.getSessionCookie());
    }
}
