package ru.baldenna.unleashagent.cli;

import ru.baldenna.unleashagent.core.UnleashAgent;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClientFactory;
import ru.baldenna.unleashagent.core.common.SynchronizerFactory;
import ru.baldenna.unleashagent.core.configuration.YamlConfigurationParser;

import java.nio.file.Files;
import java.nio.file.Path;

public class UnleashAgentCli {

    public static void main(String[] args) throws Exception {

        var cliArgsValidator = new CliArgsValidator();

        var cliArgs = cliArgsValidator.validateAngGetArgs(args);

        var yamlParser = new YamlConfigurationParser();
        var unleashClientFactory = new UnleashClientFactory();

        String yamlConfiguration = Files.readString(Path.of(cliArgs.configurationFilePath()));
        var newUnleashConfiguration = yamlParser.parse(yamlConfiguration);

        var unleashClient = unleashClientFactory.buildClient(cliArgs.unleashUrl());
        var unleashSessionManager = new UnleashSessionManager(unleashClient, cliArgs.unleashLogin(), cliArgs.unleashPassword());

        SynchronizerFactory synchronizerFactory = new SynchronizerFactory(unleashClient, unleashSessionManager);

        UnleashAgent unleashAgent = new UnleashAgent(synchronizerFactory.buildUpdaters());

        unleashAgent.synchronizeConfiguration(newUnleashConfiguration);
    }

}