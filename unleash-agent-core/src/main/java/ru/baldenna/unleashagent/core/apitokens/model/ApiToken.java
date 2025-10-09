package ru.baldenna.unleashagent.core.apitokens.model;

import java.util.List;

public record ApiToken(
        String secret,
        String tokenName,
        String type,
        String environment,
        String project,
        List<String> projects,
        String expiresAt
) {
    public ApiToken withSecret(String secret) {
        return new ApiToken(secret, tokenName, type, environment, project, projects, expiresAt);
    }
}


