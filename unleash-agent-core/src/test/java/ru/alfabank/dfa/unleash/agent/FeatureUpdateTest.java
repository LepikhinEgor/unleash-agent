package ru.alfabank.dfa.unleash.agent;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class FeatureUpdateTest extends AbstractUnleashTest {

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
    public void shouldDoNothing_whenSynchronizationCalledSecondTime() {
        // given
        var configuration = parseUnleashConfigFile("OneFeatureConfig.yaml");

        // when
        unleashAgent.synchronizeConfiguration(configuration);
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        Mockito.verify(unleashClient, Mockito.times(1)).createFeature(any(), any(), any());
        Mockito.verify(unleashClient, Mockito.times(0)).updateFeature(any(), any(), any(), any());
        Mockito.verify(unleashClient, Mockito.times(0)).deleteFeature(any(), any());
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

