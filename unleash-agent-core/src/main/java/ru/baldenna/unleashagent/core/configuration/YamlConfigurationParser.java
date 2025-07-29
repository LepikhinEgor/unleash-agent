package ru.baldenna.unleashagent.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Parsing YAML files with Unleash configuration
 */
public class YamlConfigurationParser {

    private final ObjectMapper mapper;

    public YamlConfigurationParser() {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        yamlFactory.configure(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR, true);
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
