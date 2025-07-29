package ru.baldenna.unleashagent.task;


public record UpdateFeatureTask (
    String name,
    String type,
    String description
) {}
