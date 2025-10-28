package ru.alfabank.dfa.unleash.agent.featuretags;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.dfa.unleash.agent.auth.UnleashSessionManager;
import ru.alfabank.dfa.unleash.agent.client.UnleashClient;
import ru.alfabank.dfa.unleash.agent.configuration.UnleashProjectConfiguration;
import ru.alfabank.dfa.unleash.agent.features.model.Feature;
import ru.alfabank.dfa.unleash.agent.features.model.FeaturesResponse;
import ru.alfabank.dfa.unleash.agent.tags.model.Tag;

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

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    public boolean synchronize(String projectName, UnleashProjectConfiguration newConfiguration) {
        try {
            log.info("Check unleash feature tags for update");
            var remoteFeatures = getRemoteFeatures(projectName)
                    .features().stream()
                    .collect(Collectors.toMap(Feature::name, Feature::tags));
            var localFeatures = newConfiguration.features().stream()
                    .collect(Collectors.toMap(Feature::name, Feature::tags));

            var featureTagsToCreate = getMissedFeatureTags(localFeatures, remoteFeatures);
            var featureTagsToDelete = getMissedFeatureTags(remoteFeatures, localFeatures);

            if (featureTagsToCreate.size() + featureTagsToDelete.size() != 0) {
                log.info("Feature tag states was compared. Count to create = {}, count to delete = {}",
                        featureTagsToCreate.size(), featureTagsToDelete.size());
            } else {
                log.info("Unleash features tags already up to date");
            }

            featureTagsToCreate.forEach(this::addTagToFeature);
            featureTagsToDelete.forEach(this::deleteTagFromFeature);
        } catch (Exception e) {
            log.warn("Error while feature tags synchronization in project {}", projectName);
            log.debug(e.getMessage(), e);

            return false;
        }

        return true;
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
        try {
            unleashClient.addTagToFeature(
                    featureTag.feature(),
                    new Tag(featureTag.tagValue(), featureTag.tagType()),
                    unleashSessionManager.getSessionCookie()
            );
            log.info("Tag {}:{} for feature {} was added",
                    featureTag.tagType(), featureTag.tagValue(), featureTag.feature());
        } catch (Exception e) {
            log.warn("Error adding tag {}:{} for feature {}",
                    featureTag.tagType(), featureTag.tagValue(), featureTag.feature());
            log.debug(e.getMessage(), e);
        }
    }

    private void deleteTagFromFeature(FeatureTag featureTag) {
        try {
            unleashClient.deleteTagFromFeature(
                    featureTag.feature(),
                    featureTag.tagType(),
                    featureTag.tagValue(),
                    unleashSessionManager.getSessionCookie()
            );
            log.info("Tag {}:{} for feature {} was deleted",
                    featureTag.tagType(), featureTag.tagValue(), featureTag.feature());
        } catch (Exception e) {
            log.warn("Error removing tag {}:{} from feature {}",
                    featureTag.tagType(), featureTag.tagValue(), featureTag.feature());
            log.debug(e.getMessage(), e);
        }
    }
}
