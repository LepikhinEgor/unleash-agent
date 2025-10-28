package ru.alfabank.dfa.unleash.agent.apitokens.model;

import java.util.List;

public record ApiTokenListResponse(
        List<ApiToken> tokens
) {
}


