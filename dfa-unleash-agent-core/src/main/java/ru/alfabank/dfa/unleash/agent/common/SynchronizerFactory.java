package ru.alfabank.dfa.unleash.agent.common;

import lombok.RequiredArgsConstructor;
import ru.alfabank.dfa.unleash.agent.apitokens.ApiTokenSynchronizer;
import ru.alfabank.dfa.unleash.agent.auth.UnleashSessionManager;
import ru.alfabank.dfa.unleash.agent.client.UnleashClient;
import ru.alfabank.dfa.unleash.agent.contextfields.ContextFieldSynchronizer;
import ru.alfabank.dfa.unleash.agent.features.FeatureSynchronizer;
import ru.alfabank.dfa.unleash.agent.featuretags.FeatureTagsSynchronizer;
import ru.alfabank.dfa.unleash.agent.segments.SegmentSynchronizer;
import ru.alfabank.dfa.unleash.agent.strategies.StrategySynchronizer;
import ru.alfabank.dfa.unleash.agent.tags.TagSynchronizer;

/**
 * Builds and collect all synchronizers
 * It is needed to build all synchronizers in one place,
 * to avoid modifying dependent submodules when adding a new synchronizer
 */
@SuppressWarnings("ClassDataAbstractionCoupling")
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
