package ru.baldenna.unleashagent.core;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.client.UnleashClientFactory;
import ru.baldenna.unleashagent.core.common.UnleashUpdatersFactory;
import ru.baldenna.unleashagent.core.configuration.YamlConfigurationParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


class UnleashAbstractTest {

    public static Network network = Network.newNetwork();

    private static final DockerImageName unleashImage = DockerImageName.parse("unleashorg/unleash-server");

    public static GenericContainer<?> unleashContainer = new GenericContainer<>(unleashImage)
            .withNetwork(network)
            .withNetworkAliases("unleash");

    public static final DockerImageName POSTGRESQL_IMAGE = DockerImageName.parse("postgres:16-alpine")
            .asCompatibleSubstituteFor("postgres");

    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(POSTGRESQL_IMAGE)
            .withDatabaseName("unleash")
            .withUsername("unleash_user")
            .withPassword("unleash_password")
            .withNetwork(network)
            .withNetworkAliases("postgres");


    YamlConfigurationParser yamlConfigurationParser = new YamlConfigurationParser();
    UnleashClient unleashClient;
    UnleashAgent unleashAgent;
    static {
        postgreSQLContainer.start();

        unleashContainer.addEnv("DATABASE_HOST", "postgres");
        unleashContainer.addEnv("DATABASE_NAME", "unleash");
        unleashContainer.addEnv("DATABASE_USERNAME", "unleash_user");
        unleashContainer.addEnv("DATABASE_SSL", "false");
        unleashContainer.addEnv("DATABASE_PASSWORD", "unleash_password");

        unleashContainer.setPortBindings(List.of("4242:4242"));
        unleashContainer.waitingFor(Wait.forHttp("/auth/simple/login"));
        unleashContainer.start();

    }

    UnleashAbstractTest() {

        unleashClient = new UnleashClientFactory().buildClient("http://"+unleashContainer.getHost()+":4242");
        unleashAgent = new UnleashAgent(new UnleashUpdatersFactory(unleashClient
                , new UnleashSessionManager(unleashClient, "admin", "unleash4all")).buildUpdaters());
    }


}