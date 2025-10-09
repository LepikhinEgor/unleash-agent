package ru.baldenna.unleashagent.core.apikey.model;

import java.util.List;

public record ApiTokenListResponse(
        List<ApiToken> tokens
) {
}


