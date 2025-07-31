package ru.baldenna.unleashagent.core.common.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.baldenna.unleashagent.client.UnleashClient;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnleashSessionManager {

    final UnleashClient unleashClient;

    private String sessionCookie;

    public String getUnleashSessionCookie() {
        if (sessionCookie != null) {
            return sessionCookie;
            // TODO в консольном приложении нет смысла кэшировать
            // TODO при протухании токена через 2 дня здесь будет ошибка авторизации, т.к будем отдавать тухлый токен
        }

        log.info("Session cookie not found. Trying to login");

        // TODO спрятать логин пароль
        var userResponse = unleashClient.login(new LoginRequest("admin", "1231234"));

        String unleashSessionCookie = getUnleashSessionCookie(userResponse);
        sessionCookie = unleashSessionCookie;

        log.info("Successfully logged in in unleash");

        return unleashSessionCookie;
    }

    private static String getUnleashSessionCookie(ResponseEntity<UserDTO> userResponse) {
        return Objects.requireNonNull(userResponse.getHeaders().get("Set-Cookie")).stream()
                .filter((setCookie) -> setCookie.startsWith("unleash-session="))
                .findFirst()
                .map(setCookieHeader -> setCookieHeader.substring("unleash-session=".length()))
                .orElseThrow();
    }
}
