package ru.alfabank.dfa.unleash.agent.apitokens.model;

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


