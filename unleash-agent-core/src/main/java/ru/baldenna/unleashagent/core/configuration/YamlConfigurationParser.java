package ru.baldenna.unleashagent.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.SneakyThrows;

/**
 * Parsing YAML files with Unleash configuration
 */
public class YamlConfigurationParser {

    private final ObjectMapper mapper;

    public YamlConfigurationParser() {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        mapper = new ObjectMapper(yamlFactory);
    }

    @SneakyThrows
    public UnleashConfiguration parse(String yaml) {
        return mapper.readValue(yaml, UnleashConfiguration.class);
    }

    @SneakyThrows
    public String toYaml(UnleashConfiguration configuration) {
        return mapper.writeValueAsString(configuration);
    }
}
