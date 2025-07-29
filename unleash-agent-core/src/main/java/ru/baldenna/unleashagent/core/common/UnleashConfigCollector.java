package ru.baldenna.unleashagent.core.common;

import lombok.RequiredArgsConstructor;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.configuration.UnleashProjectConfiguration;
import ru.baldenna.unleashagent.core.configuration.YamlConfigurationParser;
import ru.baldenna.unleashagent.core.contextfields.ContextFieldSynchronizer;
import ru.baldenna.unleashagent.core.projects.Project;
import ru.baldenna.unleashagent.core.projects.ProjectEnvironment;

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
                                        .map(feature -> Map.entry(feature.name(),unleashClient.getFeatureStrategies(p.name(), feature.name(), environment.name(), session)))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                                return new ProjectEnvironment(environment.name(), featureStrategies);
                            })
                            .toList();
                    return new UnleashProjectConfiguration(projectFeatures, projectEnvironments);
                }));

        return new UnleashConfiguration(tags.tags(), segments.segments(), contextFields, projects, apiTokens.tokens());
    }
}
