package ru.alfabank.dfa.unleash.agent;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.alfabank.dfa.unleash.agent.configuration.UnleashConfiguration;

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
