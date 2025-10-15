package ru.baldenna.unleashagent.core;

import org.junit.jupiter.api.Test;
import ru.baldenna.unleashagent.core.apitokens.model.CreateApiTokenRequest;
import ru.baldenna.unleashagent.core.segments.model.FeatureConstraint;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PullConfigurationTest extends AbstractUnleashTest {

    @Test
    public void testfff() {
        // given
        var project = "default";
        createFeature("test-feature", "experiment", "Old description", project);
        createTagType("custom", "Custom tag type");
        createTag("test-tag", "custom");
        addTagToFeature("test-feature", "beta", "custom");
        createSegment("beta-testers", "Segment description", "default", segmentConstraints());

        unleashClient.createApiToken(new CreateApiTokenRequest(
                "2099-01-01T00:00:00+00:00", "client", "development",
                "default", List.of("default"), "test-api-token"
        ), sessionManager.getSessionCookie());


        // when
        var yaml = unleashAgent.pullConfiguration();

        // then
        assertThat(yaml).isEqualTo("");
    }

    private static List<FeatureConstraint> segmentConstraints() {
        return List.of(
                new FeatureConstraint("userId", "IN", true, false,
                        List.of("user1", "user2", "user3", "user4", "user5"), null),
                new FeatureConstraint("environment", "NOT_IN", true, true,
                        List.of("test1", "test2"), null)
        );
    }
}
