package ru.baldenna.unleashagent.core.common;

import lombok.RequiredArgsConstructor;
import ru.baldenna.unleashagent.core.apitokens.ApiTokenSynchronizer;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.contextfields.ContextFieldSynchronizer;
import ru.baldenna.unleashagent.core.features.FeatureSynchronizer;
import ru.baldenna.unleashagent.core.featuretags.FeatureTagsSynchronizer;
import ru.baldenna.unleashagent.core.segments.SegmentSynchronizer;
import ru.baldenna.unleashagent.core.strategies.StrategySynchronizer;
import ru.baldenna.unleashagent.core.tags.TagSynchronizer;

/**
 * Builds and collect all synchronizers
 * It is needed to build all synchronizers in one place,
 * to avoid modifying dependent submodules when adding a new synchronizer
 */
@RequiredArgsConstructor
public class SynchronizerFactory {

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    public UnleashSynchronizers buildUpdaters() {

        var tagSynchronizer = new TagSynchronizer(unleashClient, unleashSessionManager);
        var segmentSynchronizer = new SegmentSynchronizer(unleashClient, unleashSessionManager);
        var contextFieldSynchronizer = new ContextFieldSynchronizer(unleashClient, unleashSessionManager);
        var featureSynchronizer = new FeatureSynchronizer(unleashClient, unleashSessionManager);
        var featureTagSynchronizer = new FeatureTagsSynchronizer(unleashClient, unleashSessionManager);
        var strategySynchronizer = new StrategySynchronizer(unleashClient, unleashSessionManager);
        var apiTokenSynchronizer = new ApiTokenSynchronizer(unleashClient, unleashSessionManager);

        return new UnleashSynchronizers(
                tagSynchronizer,
                segmentSynchronizer,
                contextFieldSynchronizer,
                featureSynchronizer,
                featureTagSynchronizer,
                strategySynchronizer,
                apiTokenSynchronizer
        );
    }
}
