package ru.baldenna.unleashagent.features;


import java.util.List;


public record FeaturesResponse(

        List<Feature> features,
        int total
) {
}
