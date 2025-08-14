package ru.baldenna.unleashagent.core.common;

import ru.baldenna.unleashagent.core.features.FeatureUpdater;
import ru.baldenna.unleashagent.core.featuretags.FeatureTagsUpdater;
import ru.baldenna.unleashagent.core.tags.TagUpdater;

public record UnleashUpdaters(
        TagUpdater tagUpdater,
        FeatureUpdater featureUpdater,
        FeatureTagsUpdater featureTagUpdater
) {
}
