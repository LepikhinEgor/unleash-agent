package ru.baldenna.unleashagent.core.segments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;

@Slf4j
@RequiredArgsConstructor
public class SegmentSynchronizer {

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    public void synchronize(UnleashConfiguration newConfiguration) {

    }
}
