package ru.baldenna.unleashagent.core;

import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.client.UnleashClientFactory;
import ru.baldenna.unleashagent.core.common.SynchronizerFactory;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.configuration.YamlConfigurationParser;
import ru.baldenna.unleashagent.core.features.model.CreateFeatureDto;
import ru.baldenna.unleashagent.core.features.model.Feature;
import ru.baldenna.unleashagent.core.tags.model.Tag;
import ru.baldenna.unleashagent.core.tagtypes.TagType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;


class UnleashAbstractTest {

    private static Network network = Network.newNetwork();

    private static final DockerImageName unleashImage = DockerImageName.parse("unleashorg/unleash-server");

    private static GenericContainer<?> unleashContainer = new GenericContainer<>(unleashImage)
            .withNetwork(network)
            .withNetworkAliases("unleash");

    private static final DockerImageName POSTGRESQL_IMAGE = DockerImageName.parse("postgres:16-alpine")
            .asCompatibleSubstituteFor("postgres");

    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(POSTGRESQL_IMAGE)
            .withDatabaseName("unleash")
            .withUsername("unleash_user")
            .withPassword("unleash_password")
            .withNetwork(network)
            .withNetworkAliases("postgres");


    protected YamlConfigurationParser yamlConfigurationParser = new YamlConfigurationParser();
    protected UnleashClient unleashClient;
    protected UnleashAgent unleashAgent;
    protected UnleashSessionManager sessionManager;

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
        unleashClient = new UnleashClientFactory().buildClient("http://" + unleashContainer.getHost() + ":4242");
        sessionManager = new UnleashSessionManager(unleashClient, "admin", "unleash4all");
        unleashAgent = new UnleashAgent(new SynchronizerFactory(unleashClient, sessionManager).buildUpdaters());
    }

    @BeforeEach
    protected void clearUnleashState() {
        // TODO replace with bulk operations
        var projectName = unleashClient.getProjects(sessionManager.getSessionCookie()).projects().getFirst().id();
        getUnleashFeatures(projectName).forEach(
                feature -> {
                    unleashClient.archiveFeature("default", feature.name(), sessionManager.getSessionCookie());
                    unleashClient.deleteFeature(feature.name(), sessionManager.getSessionCookie());
                }
        );

        getUnleashTags().forEach(tag ->
                unleashClient.deleteTag(tag.type(), tag.value(), sessionManager.getSessionCookie())
        );

        getTagTypes().stream()
                .filter(tagType -> !tagType.name().equals("simple"))
                .forEach(tagType ->
                        unleashClient.deleteTagType(tagType.name(), sessionManager.getSessionCookie()));
    }

    protected List<TagType> getTagTypes() {
        return unleashClient.getTagTypes(sessionManager.getSessionCookie()).tagTypes();
    }

    protected List<Tag> getUnleashTags() {
        return unleashClient.getTags(sessionManager.getSessionCookie()).tags();
    }

    protected List<Feature> getUnleashFeatures(String project) {
        return unleashClient.getFeatures(9999, "IS:" + project, sessionManager.getSessionCookie()).features();
    }

    protected UnleashConfiguration parseUnleashConfigFile(String filePath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        return yamlConfigurationParser.parse(Files.readString(file.toPath()));
    }

    protected void createFeature(String name, String release, String description, String project) {
        unleashClient.createFeature(project,
                new CreateFeatureDto(name, release, description, new HashSet<>()),
                sessionManager.getSessionCookie());
    }

    protected void createTag(String value, String type) {
        unleashClient.createTag(new Tag(value, type), sessionManager.getSessionCookie());
    }

    protected void addTagToFeature(String featureName, String tagValue, String tagType) {
        unleashClient.addTagToFeature(featureName, new Tag(tagValue, tagType), sessionManager.getSessionCookie());
    }

    protected void createTagType(String name, String description) {
        unleashClient.createTagType(new TagType(name, description), sessionManager.getSessionCookie());
    }

    protected String getProjectName(UnleashConfiguration configuration) {
        return configuration.projects().keySet().stream().findFirst().orElseThrow();
    }

}
