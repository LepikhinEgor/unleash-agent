package ru.alfabank.dfa.unleash.agent.auth;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.dfa.unleash.agent.client.UnleashClient;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Performs authentication in Unleash and stores actual session cookie
 */
@Slf4j
@RequiredArgsConstructor
public class UnleashSessionManager {

    private static final String UNLEASH_SESSION_COOKIE = "unleash-session=";
    private static final int SESSION_TTL_DAYS = 1;
    private final UnleashClient unleashClient;

    private final String unleashLogin;
    private final String unleashPassword;

    private UnleashSession currentSession;

    public String getSessionCookie() {
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

    private UnleashSession parseUnleashSession(Response userResponse) {
        return Objects.requireNonNull(userResponse.headers().get("Set-Cookie")).stream()
                .filter(setCookie -> setCookie.startsWith(UNLEASH_SESSION_COOKIE))
                .findFirst()
                .map(setCookieHeader -> new UnleashSession(
                        setCookieHeader.substring(UNLEASH_SESSION_COOKIE.length()),
                        ZonedDateTime.now().plusDays(SESSION_TTL_DAYS)))
                .orElseThrow();
    }
}
