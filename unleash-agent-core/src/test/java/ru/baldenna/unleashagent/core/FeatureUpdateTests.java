package ru.baldenna.unleashagent.core;

import org.junit.jupiter.api.Test;
import ru.baldenna.unleashagent.core.configuration.YamlConfigurationParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FeatureUpdateTests extends UnleashAbstractTest{

    @Test
    public void shouldCreateFeature() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("UnleashConfig.yaml").getFile());
        var configuration = yamlConfigurationParser.parse(Files.readString(file.toPath()));
        unleashAgent.synchronizeConfiguration(configuration);
    }
}

