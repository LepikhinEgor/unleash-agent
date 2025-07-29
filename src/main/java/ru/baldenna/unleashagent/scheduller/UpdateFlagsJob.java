package ru.baldenna.unleashagent.scheduller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.baldenna.unleashagent.client.UnleashClient;
import ru.baldenna.unleashagent.config.FeaturesConfig;
import ru.baldenna.unleashagent.dto.ConfigurationState;
import ru.baldenna.unleashagent.dto.CreateFeatureDto;
import ru.baldenna.unleashagent.dto.FeaturesResponse;
import ru.baldenna.unleashagent.dto.ChangeStateActions;
import ru.baldenna.unleashagent.dto.Tag;
import ru.baldenna.unleashagent.dto.UserDTO;
import ru.baldenna.unleashagent.service.StateComparator;
import ru.baldenna.unleashagent.service.RemoteStateFetcher;
import ru.baldenna.unleashagent.service.StateUpdater;
import ru.baldenna.unleashagent.task.CreateFeatureTask;
import ru.baldenna.unleashagent.task.DeleteFeatureTask;
import ru.baldenna.unleashagent.task.UpdateFeatureTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;



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
    FeaturesConfig featuresConfig;

    RemoteStateFetcher remoteStateFetcher;
    StateComparator stateComparator;
    StateUpdater stateUpdater;

    @Scheduled(fixedDelay = 10000)
    public void updateFlags() {
        var localState = new ConfigurationState(featuresConfig.features()); // TODO отдельный класс собирающий локальную конфигурацию
        var remoteState =  remoteStateFetcher.getRemoteState();

        var actions = stateComparator.compare(localState, remoteState);

        for (CreateFeatureTask flagToCreate : actions.flagsToCreate()) {
            stateUpdater.createFeature(flagToCreate);
        }

        for (UpdateFeatureTask flagToUpdate : actions.flagsToUpdate()) {
            stateUpdater.updateFeature(flagToUpdate);
        }

        for (DeleteFeatureTask flagToDelete : actions.flagsToDelete()) {
            stateUpdater.deleteFeature(flagToDelete);
        }

        //TODO создание тэгов
        //TODO удаление тэгов
        //TODO обновление тэгов у флагов через отдельную ручку
    }

}
