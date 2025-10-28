package ru.alfabank.dfa.unleash.agent.common;

import ru.alfabank.dfa.unleash.agent.apitokens.ApiTokenSynchronizer;
import ru.alfabank.dfa.unleash.agent.contextfields.ContextFieldSynchronizer;
import ru.alfabank.dfa.unleash.agent.features.FeatureSynchronizer;
import ru.alfabank.dfa.unleash.agent.featuretags.FeatureTagsSynchronizer;
import ru.alfabank.dfa.unleash.agent.segments.SegmentSynchronizer;
import ru.alfabank.dfa.unleash.agent.strategies.StrategySynchronizer;
import ru.alfabank.dfa.unleash.agent.tags.TagSynchronizer;

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
