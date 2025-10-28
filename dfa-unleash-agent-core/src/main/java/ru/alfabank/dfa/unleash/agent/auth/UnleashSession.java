package ru.alfabank.dfa.unleash.agent.auth;

import java.time.ZonedDateTime;

public record UnleashSession(
        String cookie,
        ZonedDateTime expiresAt
) {
}
