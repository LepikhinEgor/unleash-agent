package ru.baldenna.unleashagent.core.apikey.model;

import java.util.List;

public record ApiToken(
        String token,
        String tokenName,
        String type,
        String environment,
        String project,
        List<String> projects,
        String expiresAt
) {
    public ApiToken withToken(String newToken) {
        return new ApiToken(newToken, tokenName, type, environment, project, projects, expiresAt);
    }
}


