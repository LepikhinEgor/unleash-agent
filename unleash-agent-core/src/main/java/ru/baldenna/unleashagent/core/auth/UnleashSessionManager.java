package ru.baldenna.unleashagent.core.auth;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.client.UnleashClient;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Performs authentication in Unleash and stores actual session cookie
 */
@Slf4j
@RequiredArgsConstructor
public class UnleashSessionManager {

    private static final int SESSION_TTL_DAYS = 1;
    final UnleashClient unleashClient;

    final String unleashLogin;
    final String unleashPassword;

    private UnleashSession currentSession;

    public String parseUnleashSession() {
        if (alreadyAuthenticated()) {
            return currentSession.cookie();
        }

        log.info("Session cookie not found. Trying to login");

        var userResponse = unleashClient.login(new LoginRequest(unleashLogin, unleashPassword));

        currentSession = parseUnleashSession(userResponse);

        log.info("Successfully logged in in unleash");

        return currentSession.cookie();
    }

    private boolean alreadyAuthenticated() {
        return currentSession != null && currentSession.cookie() != null
                && currentSession.expiresAt().isAfter(ZonedDateTime.now());
    }

    private static UnleashSession parseUnleashSession(Response userResponse) {
        return Objects.requireNonNull(userResponse.headers().get("Set-Cookie")).stream()
                .filter((setCookie) -> setCookie.startsWith("unleash-session="))
                .findFirst()
                .map(setCookieHeader -> new UnleashSession(
                                setCookieHeader.substring("unleash-session=".length()),
                                ZonedDateTime.now().plusDays(SESSION_TTL_DAYS)))
                .orElseThrow();
    }
}
