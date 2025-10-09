package ru.baldenna.unleashagent.core.common;

import ru.baldenna.unleashagent.core.apitokens.ApiTokenSynchronizer;
import ru.baldenna.unleashagent.core.contextfields.ContextFieldSynchronizer;
import ru.baldenna.unleashagent.core.features.FeatureSynchronizer;
import ru.baldenna.unleashagent.core.featuretags.FeatureTagsSynchronizer;
import ru.baldenna.unleashagent.core.segments.SegmentSynchronizer;
import ru.baldenna.unleashagent.core.strategies.StrategySynchronizer;
import ru.baldenna.unleashagent.core.tags.TagSynchronizer;

/**
 * Container for synchronizers.
 * It is needed to combine the list of synchronizers in one place,
 * to avoid modifying dependent submodules when adding a new synchronizer.
 */
public record UnleashSynchronizers(
        TagSynchronizer tagSynchronizer,
        SegmentSynchronizer segmentSynchronizer,
        ContextFieldSynchronizer contextFieldSynchronizer,
        FeatureSynchronizer featureSynchronizer,
        FeatureTagsSynchronizer featureTagUpdater,
        StrategySynchronizer strategySynchronizer,
        ApiTokenSynchronizer apiTokenSynchronizer
) {
}
