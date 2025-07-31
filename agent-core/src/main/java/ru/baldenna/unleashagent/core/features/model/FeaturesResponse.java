package ru.baldenna.unleashagent.core.features.model;


import ru.baldenna.unleashagent.features.model.Feature;

import java.util.List;


public record FeaturesResponse(

        List<Feature> features,
        int total
) {
}
