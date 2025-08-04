package ru.baldenna.unleashagent.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;

public class YamlConfigurationParser {

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @SneakyThrows
    public UnleashConfiguration parse(String yaml) {
        return mapper.readValue(yaml, UnleashConfiguration.class);
    }
}
