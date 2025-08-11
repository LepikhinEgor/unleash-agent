package ru.baldenna.unleashagent.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FeatureUpdateTests extends UnleashAbstractTest {

    @Test
    public void shouldCreateFeature_whenFeatureNotExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("OneFeatureConfig.yaml");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualFeatures = getUnleashFeatures();

        assertThat(actualFeatures).hasSize(1);
        assertThat(actualFeatures.getFirst().name()).isEqualTo("test-feature");
        assertThat(actualFeatures.getFirst().type()).isEqualTo("release");
        assertThat(actualFeatures.getFirst().description()).isEqualTo("This is feature for test feature creation");
    }

    @Test
    public void shouldUpdateFeature_whenFeatureWithSameNameExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("OneFeatureConfig.yaml");
        createFeature("test-feature", "experiment", "Old description");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualFeatures = getUnleashFeatures();

        assertThat(actualFeatures).hasSize(1);
        assertThat(actualFeatures.getFirst().name()).isEqualTo("test-feature");
        assertThat(actualFeatures.getFirst().type()).isEqualTo("release");
        assertThat(actualFeatures.getFirst().description()).isEqualTo("This is feature for test feature creation");
    }

    @Test
    public void shouldDeleteFeature_whenFeatureNotFoundInConfigurationButExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("OneFeatureConfig.yaml");
        createFeature("test-feature", "release", "This is feature for test feature creation");
        createFeature("unknown-feature", "release", "Unknown");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualFeatures = getUnleashFeatures();

        assertThat(actualFeatures).hasSize(1);
        assertThat(actualFeatures.getFirst().name()).isEqualTo("test-feature");
        assertThat(actualFeatures.getFirst().type()).isEqualTo("release");
        assertThat(actualFeatures.getFirst().description()).isEqualTo("This is feature for test feature creation");
    }

}

