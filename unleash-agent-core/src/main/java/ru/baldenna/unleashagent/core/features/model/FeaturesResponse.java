package ru.baldenna.unleashagent.core.features.model;


import java.util.List;


public record FeaturesResponse(

        List<Feature> features,
        int total
) {
}
