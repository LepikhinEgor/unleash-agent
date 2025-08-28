package ru.baldenna.unleashagent.core.strategies.model;

import ru.baldenna.unleashagent.core.segments.model.FeatureConstraint;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Strategy(
        String id,
        String name,
        String title,
        boolean disabled,
        int sortOrder,
        List<FeatureConstraint> constraints,
        List<Variant> variants,
        Map<String, Object> parameters,
        List<Integer> segments) {

    public Strategy copyWithId(String id) {
        return new Strategy(id, name, title, disabled, sortOrder, constraints, variants, parameters, segments);
    }

    @Override
    public List<FeatureConstraint> constraints() {
        return Optional.ofNullable(constraints).orElse(Collections.emptyList());
    }

    @Override
    public List<Variant> variants() {
        return Optional.ofNullable(variants).orElse(Collections.emptyList());
    }

    @Override
    public List<Integer> segments() {
        return Optional.ofNullable(segments).orElse(Collections.emptyList());
    }
}
