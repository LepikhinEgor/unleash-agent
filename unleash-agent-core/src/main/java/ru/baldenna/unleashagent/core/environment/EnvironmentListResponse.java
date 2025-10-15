package ru.baldenna.unleashagent.core.environment;

import java.util.List;

public record EnvironmentListResponse (
        List<Environment> environments
) {}
