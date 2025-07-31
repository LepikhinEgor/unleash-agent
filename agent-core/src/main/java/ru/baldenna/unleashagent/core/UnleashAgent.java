package ru.baldenna.unleashagent.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.Builder;
import lombok.SneakyThrows;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.common.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.common.config.UnleashConfiguration;
import ru.baldenna.unleashagent.core.common.config.UnleashYamlConfiguration;
import ru.baldenna.unleashagent.core.features.FeatureUpdater;
import ru.baldenna.unleashagent.core.featuretags.FeatureTagsUpdater;
import ru.baldenna.unleashagent.core.tags.TagUpdater;

import java.io.File;

@Builder
public class UnleashAgent {

    public void updateUnleash(){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        var  unleashConfig = parseUnleashYamlConfig(mapper);
        var  unleashClient =  Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(UnleashClient.class, "http://unleash.cbclusterint.alfaintra.net/");
        var  unleashSessionManager = new UnleashSessionManager(unleashClient);

        var tagUpdater = new TagUpdater(unleashConfig, unleashClient,unleashSessionManager);
        var featureUpdater = new FeatureUpdater(unleashConfig, unleashClient,unleashSessionManager);
        var featureTagUpdater = new FeatureTagsUpdater(unleashConfig, unleashClient,unleashSessionManager);

        tagUpdater.update();
        featureUpdater.update();
        featureTagUpdater.update();
    }

    @SneakyThrows
    private UnleashConfiguration parseUnleashYamlConfig(ObjectMapper mapper)  {
        ClassLoader classLoader = getClass().getClassLoader();
        String path  = classLoader.getResource("UnleashConfig.yaml").getPath();
        return mapper.readValue(new File(path), UnleashYamlConfiguration.class).unleash();
    }
}
