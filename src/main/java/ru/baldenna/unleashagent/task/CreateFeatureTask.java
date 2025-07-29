package ru.baldenna.unleashagent.task;


import ru.baldenna.unleashagent.dto.Tag;

import java.util.HashSet;

public record CreateFeatureTask (
     String name,
     String type,
     String description,
     HashSet<Tag> tags
) {}
