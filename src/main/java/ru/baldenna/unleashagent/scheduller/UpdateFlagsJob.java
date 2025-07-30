package ru.baldenna.unleashagent.scheduller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.baldenna.unleashagent.client.UnleashClient;
import ru.baldenna.unleashagent.config.UnleashConfig;
import ru.baldenna.unleashagent.dto.ConfigurationState;
import ru.baldenna.unleashagent.service.StateComparator;
import ru.baldenna.unleashagent.service.RemoteStateFetcher;
import ru.baldenna.unleashagent.service.StateUpdateManager;


/**
 * План работы джобы:
 * 1. Получение всех данных из удаленного репозитория(Unleash)
 * 3. Сравнение с локальной конфигурацией и получение списка действий для актуализации
 * 4. Выполнение всех действий по обновлению состояния флага
 */
@Slf4j
@Component
@AllArgsConstructor
public class UpdateFlagsJob {

    UnleashClient unleashClient;
    UnleashConfig unleashConfig;

    RemoteStateFetcher remoteStateFetcher;
    StateComparator stateComparator;
    StateUpdateManager stateUpdateManager;

    @Scheduled(fixedDelay = 10000)
    public void updateFlags() {
        // TODO вынести в отдельный класс Agent
        var localState = new ConfigurationState(unleashConfig.features(), unleashConfig.tags()); // TODO отдельный класс собирающий локальную конфигурацию
        var remoteState =  remoteStateFetcher.getRemoteState();

        var actions = stateComparator.compare(localState, remoteState);

        stateUpdateManager.updateUnleash(actions);
    }

}
