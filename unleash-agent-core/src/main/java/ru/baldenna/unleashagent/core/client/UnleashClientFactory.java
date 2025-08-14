package ru.baldenna.unleashagent.core.client;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

public class UnleashClientFactory {

    public UnleashClient buildClient(String url) {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(UnleashClient.class, url);
    }
}
