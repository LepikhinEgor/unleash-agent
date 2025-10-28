package ru.alfabank.dfa.unleash.agent.environment;

import java.util.List;

public record EnvironmentListResponse (
        List<Environment> environments
) {}
