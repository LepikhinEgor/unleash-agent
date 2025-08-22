package ru.baldenna.unleashagent.example;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.baldenna.unleashagent.core.UnleashAgent;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;

@Component
public class UnleashSyncJob {

    private final UnleashAgent unleashAgent;
    private final UnleashConfiguration unleashConfiguration;

    public UnleashSyncJob(UnleashAgent unleashAgent, UnleashConfiguration unleashConfiguration) {
        this.unleashAgent = unleashAgent;
        this.unleashConfiguration = unleashConfiguration;
    }

    @Scheduled(fixedRate = 20000)
    public void sync() {
        unleashAgent.synchronizeConfiguration(unleashConfiguration);
    }
}
