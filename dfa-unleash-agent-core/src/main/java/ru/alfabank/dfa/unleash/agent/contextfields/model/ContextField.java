package ru.alfabank.dfa.unleash.agent.contextfields.model;

import java.util.Collections;
import java.util.List;

public record ContextField(
        String name,
        String description,
        boolean stickiness,
        int sortOrder,
        List<LegalValue> legalValues
) {
    @Override
    public List<LegalValue> legalValues() {
        return legalValues != null ? legalValues : Collections.emptyList();
    }
}
