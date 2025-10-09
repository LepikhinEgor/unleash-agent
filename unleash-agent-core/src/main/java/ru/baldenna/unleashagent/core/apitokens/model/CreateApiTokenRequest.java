package ru.baldenna.unleashagent.core.apitokens.model;

import java.util.List;

public record CreateApiTokenRequest(
        String expiresAt,
        String type,
        String environment,
        String project,
        List<String> projects,
        String tokenName
) {
}


