package ru.baldenna.unleashagent.core.apitokens.model;

import java.util.List;

public record ApiTokenListResponse(
        List<ApiToken> tokens
) {
}


