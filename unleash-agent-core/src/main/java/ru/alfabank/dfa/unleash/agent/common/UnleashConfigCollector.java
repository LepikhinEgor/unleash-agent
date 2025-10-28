package ru.alfabank.dfa.unleash.agent.common;

import lombok.RequiredArgsConstructor;
import ru.alfabank.dfa.unleash.agent.auth.UnleashSessionManager;
import ru.alfabank.dfa.unleash.agent.client.UnleashClient;
import ru.alfabank.dfa.unleash.agent.configuration.UnleashConfiguration;
import ru.alfabank.dfa.unleash.agent.configuration.UnleashProjectConfiguration;
import ru.alfabank.dfa.unleash.agent.configuration.YamlConfigurationParser;
import ru.alfabank.dfa.unleash.agent.contextfields.ContextFieldSynchronizer;
import ru.alfabank.dfa.unleash.agent.projects.Project;
import ru.alfabank.dfa.unleash.agent.projects.ProjectEnvironment;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UnleashConfigCollector {

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;
    private final YamlConfigurationParser yamlConfigurationParser;

    public String pullConfigurationInYaml() {
        var configuration = pullConfiguration();

        return yamlConfigurationParser.toYaml(configuration);
    }

    private UnleashConfiguration pullConfiguration() {
        String session = unleashSessionManager.getSessionCookie();
        var tags = unleashClient.getTags(session);
        var segments = unleashClient.getSegments(session);
        var contextFields = unleashClient.getContextFields(session).stream()
                .filter(contextField -> !ContextFieldSynchronizer.defaultContextFields.contains(contextField.name()))
                .collect(Collectors.toList());
        var apiTokens = unleashClient.getApiTokens(session);
        var projects = unleashClient.getProjects(session).projects().stream()
                .collect(Collectors.toMap(Project::name, p -> {
                    var features = unleashClient.getFeatures(p.name(), session);
                    var projectFeatures = unleashClient.getFeatures(p.name(), session).features();
                    var projectEnvironments = unleashClient.getProjectEnvironments(p.name(), session).environments().stream()
                            .map(environment -> {
                                var featureStrategies = features.features().stream()
                                        .map(feature -> Map.entry(feature.name(), unleashClient.getFeatureStrategies(p.name(), feature.name(), environment.name(), session)))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                                return new ProjectEnvironment(environment.name(), featureStrategies);
                            })
                            .toList();
                    return new UnleashProjectConfiguration(projectFeatures, projectEnvironments);
                }));

        return new UnleashConfiguration(tags.tags(), segments.segments(), contextFields, projects, apiTokens.tokens());
    }
}
