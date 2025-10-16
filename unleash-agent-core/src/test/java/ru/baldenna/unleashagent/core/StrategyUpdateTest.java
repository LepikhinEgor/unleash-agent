package ru.baldenna.unleashagent.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.baldenna.unleashagent.core.segments.model.FeatureConstraint;
import ru.baldenna.unleashagent.core.strategies.model.Strategy;
import ru.baldenna.unleashagent.core.strategies.model.StrategyParameters;
import ru.baldenna.unleashagent.core.strategies.model.Variant;
import ru.baldenna.unleashagent.core.strategies.model.VariantPayload;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class StrategyUpdateTest extends AbstractUnleashTest {

    @Test
    public void shouldCreateStrategy_whenStrategyNotExistsInUnleash() {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTwoStrategiesConfig.yaml");
        var projectName = getProjectName(configuration);

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var strategies = unleashClient.getFeatureStrategies(projectName, "ifelse-feature", "development", sessionManager.getSessionCookie());
        assertThat(strategies).hasSize(1);
        var strategy = strategies.getFirst();
        assertThat(strategy.name()).isEqualTo("flexibleRollout");
        assertThat(strategy.parameters().rollout()).isEqualTo("100");
        assertThat(strategy.constraints()).hasSize(1);
        assertThat(strategy.constraints().getFirst()).isEqualTo(new FeatureConstraint("age", "NUM_GTE", true, false, Collections.emptyList(), "40"));
    }

    @Test
    public void shouldDoNothing_whenSynchronizationCalledSecondTime() {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTwoStrategiesConfig.yaml");

        // when
        unleashAgent.synchronizeConfiguration(configuration);
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        Mockito.verify(unleashClient, Mockito.times(2)).addFeatureStrategy(any(), any(), any(), any(), any());
        Mockito.verify(unleashClient, Mockito.times(0)).updateFeatureStrategy(any(), any(), any(), any(), any(), any());
        Mockito.verify(unleashClient, Mockito.times(0)).updateFeatureStrategy(any(), any(), any(), any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("staleStrategies")
    public void shouldUpdateStrategy_whenStrategyWithSameNameExistsInUnleash(StrategyUpdateCase strategyUpdateCase) {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTwoStrategiesConfig.yaml");
        var projectName = getProjectName(configuration);
        createFeature("variants-feature", "release", "Variants based feature", projectName);
        // create stale strategy
        unleashClient.addFeatureStrategy(projectName, "variants-feature", "development",
                strategyUpdateCase.toStrategy(),
                sessionManager.getSessionCookie());
        var oldStrategy = unleashClient.getFeatureStrategies(projectName, "variants-feature", "development", sessionManager.getSessionCookie()).getFirst();

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var strategies = unleashClient.getFeatureStrategies(projectName, "variants-feature", "development", sessionManager.getSessionCookie());
        assertThat(strategies).hasSize(1);
        var updatedStrategy = strategies.getFirst();
        assertThat(updatedStrategy.id()).isEqualTo(oldStrategy.id());
        assertThat(updatedStrategy.title()).isEqualTo("New title");
        assertThat(updatedStrategy.parameters().rollout()).isEqualTo("100");
        assertThat(updatedStrategy.variants()).hasSize(3);
        assertThat(updatedStrategy.variants().getFirst()).isEqualTo(new Variant("firstVariant", 200, "fix", "default", new VariantPayload("string", "testFirst")));
        assertThat(updatedStrategy.variants().get(1)).isEqualTo(new Variant("secondVariant", 500, "fix", "default", new VariantPayload("string", "testSecond")));
        assertThat(updatedStrategy.variants().get(2)).isEqualTo(new Variant("thirdVariant", 300, "variable", "default", new VariantPayload("string", "testThird")));
        assertThat(updatedStrategy.constraints()).hasSize(2);
        assertThat(updatedStrategy.constraints().getFirst()).isEqualTo(new FeatureConstraint("age", "NUM_GTE", true, false, Collections.emptyList(), "40"));
        assertThat(updatedStrategy.constraints().get(1)).isEqualTo( new FeatureConstraint("tariff", "IN", true, false, List.of("ULTIMATE", "VIP"), null));

        Mockito.verify(unleashClient, Mockito.times(1)).updateFeatureStrategy(any(), any(), any(), any(), any(), any());
    }

    @Test
    public void shouldDeleteStrategy_whenStrategyNotFoundInConfigurationButExistsInUnleash() {
        // given
        var configuration = parseUnleashConfigFile("FeatureWithTwoStrategiesConfig.yaml");
        var projectName = getProjectName(configuration);
        createFeature("beans-feature", "release", "Unknown", projectName);
        unleashClient.addFeatureStrategy(projectName, "beans-feature", "development",
                new Strategy(null,
                        "flexibleRollout",
                        null,
                        false,
                        List.of(),
                        List.of(),
                        new StrategyParameters("100", "default", "beans-feature"),
                        List.of()),
                sessionManager.getSessionCookie());

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var strategies = unleashClient.getFeatureStrategies(projectName, "beans-feature", "development", sessionManager.getSessionCookie());
        assertThat(strategies).isEmpty();
    }


    static List<StrategyUpdateCase> staleStrategies() {
        return List.of(
                strategyFromFile("changed title").withTitle("Old title"),
                strategyFromFile("changed rollout").withParameters(new StrategyParameters("50", "default", "variants-feature")),
                strategyFromFile("added constraint").withConstraints(
                        List.of(
                                new FeatureConstraint("age", "NUM_GTE", true, false, null, "40"),
                                new FeatureConstraint("tariff", "IN", true, false, List.of("ULTIMATE", "VIP"), null),
                                new FeatureConstraint("userId", "IN", true, false, List.of("123"), null)
                        )
                ),
                strategyFromFile("removed constraint").withConstraints(
                        List.of(
                                new FeatureConstraint("age", "NUM_GTE", true, false, null, "40")
                        )
                ),
                strategyFromFile("changed constraint value").withConstraints(
                        List.of(
                                new FeatureConstraint("age", "NUM_GTE", true, false, null, "100"),
                                new FeatureConstraint("tariff", "IN", true, false, List.of("ULTIMATE", "VIP"), null)
                        )
                ),
                strategyFromFile("add constraint value").withConstraints(
                        List.of(
                                new FeatureConstraint("age", "NUM_GTE", true, false, null, "40"),
                                new FeatureConstraint("tariff", "IN", true, false, List.of("ULTIMATE", "VIP", "FREE"), null)
                        )
                ),
                strategyFromFile("remove constraint value").withConstraints(
                        List.of(
                                new FeatureConstraint("age", "NUM_GTE", true, false, null, "40"),
                                new FeatureConstraint("tariff", "IN", true, false, List.of("ULTIMATE"), null)
                        )
                ),
                strategyFromFile("change constraint operator").withConstraints(
                        List.of(
                                new FeatureConstraint("age", "NUM_LTE", true, false, null, "40"),
                                new FeatureConstraint("tariff", "IN", true, false, List.of("ULTIMATE", "VIP"), null)
                        )
                ),
                strategyFromFile("change constraint caseInsensitive").withConstraints(
                        List.of(
                                new FeatureConstraint("age", "NUM_GTE", false, false, null, "40"),
                                new FeatureConstraint("tariff", "IN", true, false, List.of("ULTIMATE", "VIP"), null)
                        )
                ),
                strategyFromFile("change constraint false").withConstraints(
                        List.of(
                                new FeatureConstraint("age", "NUM_GTE", true, true, null, "40"),
                                new FeatureConstraint("tariff", "IN", true, false, List.of("ULTIMATE", "VIP"), null)
                        )
                ),
                strategyFromFile("changed variant weightType").withVariants(
                        List.of(
                                new Variant("firstVariant", 200, "fix", "default", new VariantPayload("string", "testFirst")),
                                new Variant("secondVariant", 500, "variable", "default", new VariantPayload("string", "testSecond")),
                                new Variant("thirdVariant", 300, "variable", "default", new VariantPayload("string", "testThird"))
                        )
                ),
                strategyFromFile("changed variant payload").withVariants(
                        List.of(
                                new Variant("firstVariant", 200, "fix", "default", new VariantPayload("string", "testFirst")),
                                new Variant("secondVariant", 500, "fix", "default", new VariantPayload("string", "changed")),
                                new Variant("thirdVariant", 300, "variable", "default", new VariantPayload("string", "testThird"))
                        )
                ),
                strategyFromFile("added variant").withVariants(
                        List.of(
                                new Variant("firstVariant", 200, "fix", "default", new VariantPayload("string", "testFirst")),
                                new Variant("secondVariant", 500, "fix", "default", new VariantPayload("string", "changed")),
                                new Variant("thirdVariant", 100, "variable", "default", new VariantPayload("string", "testThird")),
                                new Variant("fourthVariant", 200, "variable", "default", new VariantPayload("string", "testFourth"))
                        )
                ),
                strategyFromFile("removed variant").withVariants(
                        List.of(
                                new Variant("firstVariant", 200, "fix", "default", new VariantPayload("string", "testFirst")),
                                new Variant("secondVariant", 800, "variable", "default", new VariantPayload("string", "testSecond"))
                        )
                )
        );
    }

    @Data
    @AllArgsConstructor
    @With
    public static class StrategyUpdateCase {
        String caseDescription;
        String id;
        String name;
        String title;
        boolean disabled;
        List<FeatureConstraint> constraints;
        List<Variant> variants;
        StrategyParameters parameters;
        List<Integer> segments;

        public Strategy toStrategy() {
            return new Strategy(id, name, title, disabled, constraints, variants, parameters, segments);
        }

        @Override
        public String toString() {
            return caseDescription;
        }
    }

    static StrategyUpdateCase strategyFromFile(String caseDescription) {
        return new StrategyUpdateCase(caseDescription, null, "flexibleRollout", "New title", false,
                List.of(
                    new FeatureConstraint("age", "NUM_GTE", true, false, null, "40"),
                    new FeatureConstraint("tariff", "IN", true, false, List.of("ULTIMATE", "VIP"), null)
                ),
                List.of(
                        new Variant("firstVariant", 200, "fix", "default", new VariantPayload("string", "testFirst")),
                        new Variant("secondVariant", 500, "fix", "default", new VariantPayload("string", "testSecond")),
                        new Variant("thirdVariant", 300, "variable", "default", new VariantPayload("string", "testThird"))
                ),
                new StrategyParameters("100", "default", "variants-feature"),
                List.of());
    }

}
