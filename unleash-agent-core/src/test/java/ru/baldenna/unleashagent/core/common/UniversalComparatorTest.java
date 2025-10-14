package ru.baldenna.unleashagent.core.common;

import org.junit.jupiter.api.Test;
import ru.baldenna.unleashagent.core.segments.model.FeatureConstraint;
import ru.baldenna.unleashagent.core.segments.model.Segment;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UniversalComparatorTest {

    UniversalComparator comparator = new UniversalComparator();

    @Test
    public void testfr() {
        var first = new Segment(1, "name1", "description1", List.of(
                new FeatureConstraint("appName", "EQ", true, false, List.of("test-app"), "value"),
                new FeatureConstraint("appName", "IN", true, false, List.of("test-app"), "value")),
                "default");
        var second = new Segment(1, "name1", "description1", List.of(
                new FeatureConstraint("appName", "IN", true, false, List.of("test-app"), "value"),
                new FeatureConstraint("appName", "EQ", true, false, List.of("test-app"), "value")),
                "default");

        assertThat(comparator.compareWithLib(first, second)).isFalse();
    }

}