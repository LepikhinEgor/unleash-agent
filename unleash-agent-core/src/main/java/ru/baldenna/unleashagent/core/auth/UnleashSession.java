package ru.baldenna.unleashagent.core.auth;

import java.time.ZonedDateTime;

public record UnleashSession(
        String cookie,
        ZonedDateTime expiresAt
) {
}
