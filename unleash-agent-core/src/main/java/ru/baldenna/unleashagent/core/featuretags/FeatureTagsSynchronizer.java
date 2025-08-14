package ru.baldenna.unleashagent.core.featuretags;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.configuration.UnleashProjectConfiguration;
import ru.baldenna.unleashagent.core.features.model.Feature;
import ru.baldenna.unleashagent.core.features.model.FeaturesResponse;
import ru.baldenna.unleashagent.core.tags.model.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Synchronizes feature tags in Unleash with feature tags in the given configuration
 */
@Slf4j
@AllArgsConstructor
public class FeatureTagsSynchronizer {

    final UnleashClient unleashClient;
    final UnleashSessionManager unleashSessionManager;

    public void synchronize(String projectName, UnleashProjectConfiguration newConfiguration) {
        log.info("Check unleash feature tags for update");
        var remoteFeaturesWithTags = getRemoteFeatures(projectName)
                .features().stream()
                .collect(Collectors.toMap(Feature::name, Feature::tags));
        var localFeaturesWithTags = newConfiguration.features().stream()
                .collect(Collectors.toMap(Feature::name, Feature::tags));

        var featureTagsToCreate = getMissedFeatureTags(localFeaturesWithTags, remoteFeaturesWithTags);
        var featureTagsToDelete = getMissedFeatureTags(remoteFeaturesWithTags, localFeaturesWithTags);

        if (featureTagsToCreate.size() + featureTagsToDelete.size() != 0) {
            log.info("Feature tag states was compared. Count to create = {}, count to delete = {}",
                    featureTagsToCreate.size(), featureTagsToDelete.size());
        } else {
            log.info("Unleash features tags already up to date");
        }

        featureTagsToCreate.forEach(this::addTagToFeature);
        featureTagsToDelete.forEach(this::deleteTagFromFeature);
    }

    private FeaturesResponse getRemoteFeatures(String projectName) {
        return unleashClient.getFeatures(projectName, unleashSessionManager.getSessionCookie());
    }

    /**
     * Compares two maps of features with tags
     * Returns a list of FeatureTag that are present in the origin map but not in the target map.
     */
    private List<FeatureTag> getMissedFeatureTags(Map<String, HashSet<Tag>> originFeaturesWithTags,
                                                  Map<String, HashSet<Tag>> targetFeaturesWithTags) {
        var featuresWithExtraTag = new ArrayList<FeatureTag>();
        originFeaturesWithTags.forEach((featureName, localTags) -> {
            HashSet<Tag> remoteTags = targetFeaturesWithTags.get(featureName);
            localTags.stream()
                    .filter(localTag -> !remoteTags.contains(localTag))
                    .forEach(localTag -> featuresWithExtraTag.add(
                            new FeatureTag(featureName, localTag.value(), localTag.type()))
                    );
        });

        return featuresWithExtraTag;
    }

    private void addTagToFeature(FeatureTag featureTag) {
        unleashClient.addTagToFeature(
                featureTag.feature(),
                new Tag(featureTag.tagValue(), featureTag.tagType()),
                unleashSessionManager.getSessionCookie()
        );
        log.info("Tag {}:{} for feature {} was added",
                featureTag.tagType(), featureTag.tagValue(), featureTag.feature());
    }

    private void deleteTagFromFeature(FeatureTag featureTag) {
        unleashClient.deleteTagFromFeature(
                featureTag.feature(),
                featureTag.tagType(),
                featureTag.tagValue(),
                unleashSessionManager.getSessionCookie()
        );
        log.info("Tag {}:{} for feature {} was deleted",
                featureTag.tagType(), featureTag.tagValue(), featureTag.feature());
    }
}
