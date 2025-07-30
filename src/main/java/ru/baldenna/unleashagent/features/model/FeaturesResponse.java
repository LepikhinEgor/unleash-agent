package ru.baldenna.unleashagent.features.model;


import java.util.List;


public record FeaturesResponse(

        List<Feature> features,
        int total
) {
}
