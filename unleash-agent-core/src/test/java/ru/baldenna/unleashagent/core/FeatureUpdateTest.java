package ru.baldenna.unleashagent.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FeatureUpdateTest extends UnleashAbstractTest {

    @Test
    public void shouldCreateFeature_whenFeatureNotExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("OneFeatureConfig.yaml");
        var projectName = getProjectName(configuration);

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualFeatures = getUnleashFeatures(projectName);

        assertThat(actualFeatures).hasSize(1);
        assertThat(actualFeatures.getFirst().name()).isEqualTo("test-feature");
        assertThat(actualFeatures.getFirst().type()).isEqualTo("release");
        assertThat(actualFeatures.getFirst().description()).isEqualTo("This is feature for test feature creation");
    }

    @Test
    public void shouldUpdateFeature_whenFeatureWithSameNameExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("OneFeatureConfig.yaml");
        var projectName = getProjectName(configuration);
        createFeature("test-feature", "experiment", "Old description", projectName);

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualFeatures = getUnleashFeatures(projectName);

        assertThat(actualFeatures).hasSize(1);
        assertThat(actualFeatures.getFirst().name()).isEqualTo("test-feature");
        assertThat(actualFeatures.getFirst().type()).isEqualTo("release");
        assertThat(actualFeatures.getFirst().description()).isEqualTo("This is feature for test feature creation");
    }

    @Test
    public void shouldDeleteFeature_whenFeatureNotFoundInConfigurationButExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("OneFeatureConfig.yaml");
        var projectName = getProjectName(configuration);
        createFeature("test-feature", "release", "This is feature for test feature creation", projectName);
        createFeature("unknown-feature", "release", "Unknown", projectName);

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualFeatures = getUnleashFeatures(projectName);

        assertThat(actualFeatures).hasSize(1);
        assertThat(actualFeatures.getFirst().name()).isEqualTo("test-feature");
        assertThat(actualFeatures.getFirst().type()).isEqualTo("release");
        assertThat(actualFeatures.getFirst().description()).isEqualTo("This is feature for test feature creation");
    }

}

