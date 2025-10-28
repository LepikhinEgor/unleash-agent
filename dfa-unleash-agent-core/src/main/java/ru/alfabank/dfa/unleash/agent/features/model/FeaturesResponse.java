package ru.alfabank.dfa.unleash.agent.features.model;


import java.util.List;


public record FeaturesResponse(

        List<Feature> features,
        int total
) {
}
