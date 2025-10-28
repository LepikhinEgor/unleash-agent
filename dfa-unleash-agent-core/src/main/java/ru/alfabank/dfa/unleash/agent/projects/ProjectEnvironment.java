package ru.alfabank.dfa.unleash.agent.projects;

import ru.alfabank.dfa.unleash.agent.strategies.model.Strategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ProjectEnvironment(
        String name,
        Map<String, List<Strategy>> featureStrategies
) {

    @Override
    public Map<String, List<Strategy>> featureStrategies() {
        return Optional.ofNullable(featureStrategies).orElse(Collections.emptyMap());
    }
}
