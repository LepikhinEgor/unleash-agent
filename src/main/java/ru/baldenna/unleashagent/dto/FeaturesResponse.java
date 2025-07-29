package ru.baldenna.unleashagent.dto;


import java.util.List;


public record FeaturesResponse(

        List<Feature> features,
        int total
) {
}
