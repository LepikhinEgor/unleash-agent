package ru.alfabank.dfa.unleash.agent;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class FeatureTagUpdateTest extends AbstractUnleashTest {

    @Test
    public void shouldCreateFeatureTag_whenFeatureTagNotExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTagConfig.yaml");
        var projectName = getProjectName(configuration);
        createFeature("test-feature", "release", "This is feature for test feature creation", projectName);

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var featureTags = getUnleashFeatures(projectName).getFirst().tags();

        assertThat(featureTags).hasSize(1);
        assertThat(featureTags).anyMatch(tag -> tag.value().equals("beta") && tag.type().equals("simple"));
    }

    @Test
    public void shouldDoNothing_whenSynchronizationCalledSecondTime() {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTagConfig.yaml");
        var projectName = getProjectName(configuration);
        createFeature("test-feature", "release", "This is feature for test feature creation", projectName);

        // when
        unleashAgent.synchronizeConfiguration(configuration);
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        Mockito.verify(unleashClient, Mockito.times(1)).addTagToFeature(any(), any(), any());
        Mockito.verify(unleashClient, Mockito.times(0)).deleteTagFromFeature(any(), any(), any(), any());
    }

    @Test
    public void shouldReplaceFeatureTag_whenFeatureTagWithSameNameButOtherTypeExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTagConfig.yaml");
        var projectName = getProjectName(configuration);
        createFeature("test-feature", "release", "This is feature for test feature creation", projectName);
        createTagType("custom", "Custom tag type");
        addTagToFeature("test-feature", "beta", "custom");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var featureTags = getUnleashFeatures(projectName).getFirst().tags();

        assertThat(featureTags).hasSize(1);
        assertThat(featureTags).anyMatch(tag -> tag.value().equals("beta") && tag.type().equals("simple"));
    }

    @Test
    public void shouldDeleteFeatureTag_whenFeatureTagNotFoundInConfigurationButExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTagConfig.yaml");
        var projectName = getProjectName(configuration);
        createFeature("test-feature", "release", "This is feature for test feature creation", projectName);
        addTagToFeature("test-feature", "alpha", "simple");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var featureTags = getUnleashFeatures(projectName).getFirst().tags();

        assertThat(featureTags).hasSize(1);
        assertThat(featureTags).anyMatch(tag -> tag.value().equals("beta") && tag.type().equals("simple"));
    }
}
