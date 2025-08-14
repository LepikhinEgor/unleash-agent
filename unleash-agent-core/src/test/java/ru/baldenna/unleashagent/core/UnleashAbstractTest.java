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
import ru.baldenna.unleashagent.core.common.UnleashUpdatersFactory;
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
    UnleashSessionManager sessionManager;

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
        unleashAgent = new UnleashAgent(new UnleashUpdatersFactory(unleashClient, sessionManager).buildUpdaters());
    }

    @BeforeEach
    protected void clearUnleashState() {
        // TODO replace with bulk operations
        var projectName = unleashClient.getProjects(sessionManager.getUnleashSessionCookie()).projects().getFirst().id();
        getUnleashFeatures(projectName).forEach(
                feature -> {
                    unleashClient.archiveFeature("default", feature.name(), sessionManager.getUnleashSessionCookie());
                    unleashClient.deleteFeature(feature.name(), sessionManager.getUnleashSessionCookie());
                }
        );

        getUnleashTags().forEach(tag ->
                unleashClient.deleteTag(tag.type(), tag.value(), sessionManager.getUnleashSessionCookie())
        );

        getTagTypes().stream()
                .filter(tagType -> !tagType.name().equals("simple"))
                .forEach(tagType ->
                        unleashClient.deleteTagType(tagType.name(), sessionManager.getUnleashSessionCookie()));
    }

    protected List<TagType> getTagTypes() {
        return unleashClient.getTagTypes(sessionManager.getUnleashSessionCookie()).tagTypes();
    }

    protected List<Tag> getUnleashTags() {
        return unleashClient.getTags(sessionManager.getUnleashSessionCookie()).tags();
    }

    protected List<Feature> getUnleashFeatures(String project) {
        return unleashClient.getFeatures(9999, "IS:" + project, sessionManager.getUnleashSessionCookie()).features();
    }

    protected UnleashConfiguration parseUnleashConfigFile(String filePath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        return yamlConfigurationParser.parse(Files.readString(file.toPath()));
    }

    protected void createFeature(String name, String release, String description, String project) {
        unleashClient.createFeature(project,
                new CreateFeatureDto(name, release, description, new HashSet<>()),
                sessionManager.getUnleashSessionCookie());
    }

    protected void createTag(String value, String type) {
        unleashClient.createTag(new Tag(value, type), sessionManager.getUnleashSessionCookie());
    }

    protected void addTagToFeature(String featureName, String tagValue, String tagType) {
        unleashClient.addTagToFeature(featureName, new Tag(tagValue, tagType), sessionManager.getUnleashSessionCookie());
    }

    protected void createTagType(String name, String description) {
        unleashClient.createTagType(new TagType(name, description), sessionManager.getUnleashSessionCookie());
    }

    protected String getProjectName(UnleashConfiguration configuration) {
        return configuration.projects().keySet().stream().findFirst().orElseThrow();
    }

}