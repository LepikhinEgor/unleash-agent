package ru.alfabank.dfa.unleash.agent;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class TagUpdateTest extends AbstractUnleashTest {

    @Test
    public void shouldCreateTag_whenTagNotExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("OneTagConfig.yaml");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualTags = getUnleashTags();

        assertThat(actualTags).hasSize(1);
        assertThat(actualTags.getFirst().type()).isEqualTo("simple");
        assertThat(actualTags.getFirst().value()).isEqualTo("test-tag");
    }

    @Test
    public void shouldDoNothing_whenSynchronizationCalledSecondTime() {
        // given
        var configuration = parseUnleashConfigFile("OneTagConfig.yaml");

        // when
        unleashAgent.synchronizeConfiguration(configuration);
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        Mockito.verify(unleashClient, Mockito.times(1)).createTag(any(), any());
        Mockito.verify(unleashClient, Mockito.times(0)).deleteTag(any(), any(), any());
    }

    @Test
    public void shouldReplaceTag_whenTagWithSameNameButOtherTypeExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("OneTagConfig.yaml");
        createTagType("custom", "Custom tag type");
        createTag("test-tag", "custom");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualTags = getUnleashTags();

        assertThat(actualTags).hasSize(1);
        assertThat(actualTags).anyMatch(tag -> tag.value().equals("test-tag") && tag.type().equals("simple"));
    }

    @Test
    public void shouldDeleteTag_whenTagNotFoundInConfigurationButExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("OneTagConfig.yaml");
        createTag("test-tag", "simple");
        createTag("another-tag", "simple");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualTags = getUnleashTags();

        assertThat(actualTags).hasSize(1);
        assertThat(actualTags.getFirst().type()).isEqualTo("simple");
        assertThat(actualTags.getFirst().value()).isEqualTo("test-tag");
    }
}
