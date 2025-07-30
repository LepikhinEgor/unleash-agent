package ru.baldenna.unleashagent.common.scheduller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.baldenna.unleashagent.features.FeatureUpdater;
import ru.baldenna.unleashagent.tags.TagUpdater;


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

    FeatureUpdater featureUpdater;
    TagUpdater tagUpdater;

    @Scheduled(fixedDelay = 10000)
    public void updateFlags() {
        // TODO вынести в отдельный класс Agent
        tagUpdater.update();
        featureUpdater.update();
    }

}
