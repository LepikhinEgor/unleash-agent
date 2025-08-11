package ru.baldenna.unleashagent.core;

import org.junit.jupiter.api.Test;
import ru.baldenna.unleashagent.core.tags.model.Tag;
import ru.baldenna.unleashagent.core.tagtypes.TagType;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FeatureTagUpdateTests extends UnleashAbstractTest{

    @Test
    public void shouldCreateFeatureTag_whenFeatureTagNotExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTagConfig.yaml");
        createFeature("test-feature", "release", "This is feature for test feature creation");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var featureTags = getUnleashFeatures().getFirst().tags();

        assertThat(featureTags).hasSize(1);
        assertThat(featureTags).anyMatch(tag -> tag.value().equals("beta") && tag.type().equals("simple"));
    }

    @Test
    public void shouldReplaceFeatureTag_whenFeatureTagWithSameNameButOtherTypeExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTagConfig.yaml");
        createFeature("test-feature", "release", "This is feature for test feature creation");
        createTagType("custom", "Custom tag type");
        addTagToFeature("test-feature","beta", "custom");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var featureTags = getUnleashFeatures().getFirst().tags();

        assertThat(featureTags).hasSize(1);
        assertThat(featureTags).anyMatch(tag -> tag.value().equals("beta") && tag.type().equals("simple"));
    }

    @Test
    public void shouldDeleteFeatureTag_whenFeatureTagNotFoundInConfigurationButExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTagConfig.yaml");
        createFeature("test-feature", "release", "This is feature for test feature creation");
        addTagToFeature("test-feature", "alpha", "simple");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var featureTags = getUnleashFeatures().getFirst().tags();

        assertThat(featureTags).hasSize(1);
        assertThat(featureTags).anyMatch(tag -> tag.value().equals("beta") && tag.type().equals("simple"));
    }
}
