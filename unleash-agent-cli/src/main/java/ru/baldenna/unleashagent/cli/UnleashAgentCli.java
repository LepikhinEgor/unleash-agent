package ru.baldenna.unleashagent.cli;

import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClientFactory;
import ru.baldenna.unleashagent.core.configuration.YamlConfigurationParser;
import ru.baldenna.unleashagent.core.features.FeatureUpdater;
import ru.baldenna.unleashagent.core.featuretags.FeatureTagsUpdater;
import ru.baldenna.unleashagent.core.tags.TagUpdater;

import java.nio.file.Files;
import java.nio.file.Path;

public class UnleashAgentCli {

    public static void main(String[] args) throws Exception {

        var cliArgsValidator = new CliArgsValidator();

        var cliArgs = cliArgsValidator.validateAngGetArgs(args);

        var yamlParser = new YamlConfigurationParser();
        var unleashClientFactory = new UnleashClientFactory();

        var unleashConfig = yamlParser.parse(Files.readString(Path.of(cliArgs.configurationFilePath())));
        var unleashClient = unleashClientFactory.buildClient(cliArgs.unleashUrl());
        var unleashSessionManager = new UnleashSessionManager(unleashClient);

        var tagUpdater = new TagUpdater(unleashConfig, unleashClient, unleashSessionManager);
        var featureUpdater = new FeatureUpdater(unleashConfig, unleashClient, unleashSessionManager);
        var featureTagUpdater = new FeatureTagsUpdater(unleashConfig, unleashClient, unleashSessionManager);

        tagUpdater.update();
        featureUpdater.update();
        featureTagUpdater.update();
    }

}