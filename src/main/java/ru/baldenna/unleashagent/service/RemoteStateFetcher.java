package ru.baldenna.unleashagent.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.baldenna.unleashagent.client.UnleashClient;
import ru.baldenna.unleashagent.dto.ConfigurationState;
import ru.baldenna.unleashagent.dto.FeaturesResponse;
import ru.baldenna.unleashagent.dto.LoginRequest;
import ru.baldenna.unleashagent.dto.UserDTO;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class RemoteStateFetcher {

    UnleashClient unleashClient;
    UnleashSessionManager unleashSessionManager;

    public ConfigurationState getRemoteState() {
        String unleashSessionCookie = unleashSessionManager.getUnleashSessionCookie();

        var features = unleashClient.getFeatures(20, "IS:default", unleashSessionCookie);

        log.info("Remote configuration fetched");
        return new ConfigurationState(features.getBody().features());
    }

}
