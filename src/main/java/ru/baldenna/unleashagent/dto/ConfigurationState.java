package ru.baldenna.unleashagent.dto;

import java.util.List;

public record ConfigurationState (
        List<Feature> features
) {}
