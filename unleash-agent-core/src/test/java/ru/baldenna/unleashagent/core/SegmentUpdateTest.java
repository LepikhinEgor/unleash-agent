package ru.baldenna.unleashagent.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.baldenna.unleashagent.core.segments.model.Segment;
import ru.baldenna.unleashagent.core.segments.model.SegmentConstraint;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SegmentUpdateTest extends AbstractUnleashTest {

    @Test
    public void shouldCreateSegment_whenSegmentNotExistsInUnleash() {
        // given
        var configuration = parseUnleashConfigFile("OneSegmentConfig.yaml");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualSegments = getUnleashSegments();

        assertSegmentsSynchronized(actualSegments);
    }

    @ParameterizedTest
    @MethodSource("staleSegments")
    public void shouldUpdateSegment_whenSegmentWithSameNameExistsInUnleash(Segment staleSegment) {
        // given
        var configuration = parseUnleashConfigFile("OneSegmentConfig.yaml");
        createSegment(staleSegment.name(), staleSegment.description(), staleSegment.project(),
                staleSegment.constraints());

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualSegments = getUnleashSegments();

        assertSegmentsSynchronized(actualSegments);
    }

    @Test
    public void shouldDeleteSegment_whenSegmentNotFoundInConfigurationButExistsInUnleash() {
        // given
        var configuration = parseUnleashConfigFile("OneSegmentConfig.yaml");
        createSegment("beta-testers", "Old description", "default",
                List.of(new SegmentConstraint("appName", "IN", true, false, List.of("test-app"))));
        createSegment("unknown-segment", "Old description", "default",
                List.of(new SegmentConstraint("appName", "IN", true, false, List.of("test-app"))));


        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var actualSegments = getUnleashSegments();

        assertSegmentsSynchronized(actualSegments);
    }

    private static void assertSegmentsSynchronized(List<Segment> actualSegments) {
        assertThat(actualSegments).hasSize(1);
        Segment segment = actualSegments.getFirst();
        assertThat(segment.name()).isEqualTo("beta-testers");
        assertThat(segment.description()).isEqualTo("Beta testers which test unreleased features");
        assertThat(segment.project()).isEqualTo("default");
        assertThat(segment.constraints()).hasSize(2);
        SegmentConstraint firstConstraint = segment.constraints().get(0);
        assertThat(firstConstraint.contextName()).isEqualTo("userId");
        assertThat(firstConstraint.operator()).isEqualTo("IN");
        assertThat(firstConstraint.caseInsensitive()).isTrue();
        assertThat(firstConstraint.inverted()).isFalse();
        assertThat(firstConstraint.values()).hasSize(5);
        assertThat(firstConstraint.values()).containsAll(List.of("user1", "user2", "user3", "user4", "user5"));
    }

    @SuppressWarnings("checkstyle:linelength")
    static List<Segment> staleSegments() {
        return List.of(
                new Segment(1, "beta-testers",
                        "Old description", actualConstraints(), "default"),
                new Segment(1, "beta-testers",
                        "Beta testers which test unreleased features", actualConstraints(), null),
                new Segment(1, "beta-testers",
                        "Beta testers which test unreleased features", actualConstraints().subList(0, 1), "default"),
                new Segment(1, "beta-testers",
                        "Beta testers which test unreleased features", staleConstraints(), "default")
        );
    }

    private static List<SegmentConstraint> actualConstraints() {
        return List.of(
                new SegmentConstraint("userId", "IN", true, false,
                        List.of("user1", "user2", "user3", "user4", "user5")),
                new SegmentConstraint("environment", "NOT_IN", true, true,
                        List.of("test1", "test2"))
        );
    }

    private static List<SegmentConstraint> staleConstraints() {
        return List.of(
                new SegmentConstraint("userId", "IN", true, false, List.of("user1", "user2", "user3", "user4")),
                new SegmentConstraint("environment", "NOT_IN", true, true, List.of("test1"))
        );
    }
}
