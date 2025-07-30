package ru.baldenna.unleashagent.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.baldenna.unleashagent.dto.UpdateStateActions;
import ru.baldenna.unleashagent.task.CreateFeatureTask;
import ru.baldenna.unleashagent.task.CreateTagTask;
import ru.baldenna.unleashagent.task.DeleteFeatureTask;
import ru.baldenna.unleashagent.task.DeleteTagTask;
import ru.baldenna.unleashagent.task.UpdateFeatureTask;

@Service
@AllArgsConstructor
public class StateUpdateManager {

    UnleashStateUpdater unleashStateUpdater;

    public void updateUnleash(UpdateStateActions actions) {

        for (CreateTagTask createTagTask : actions.tagsToCreate()) {
            unleashStateUpdater.createTag(createTagTask);
        }

        for (DeleteTagTask deleteTagTask : actions.tagsToDelete()) {
            unleashStateUpdater.deleteTag(deleteTagTask);
        }

        for (CreateFeatureTask flagToCreate : actions.flagsToCreate()) {
            unleashStateUpdater.createFeature(flagToCreate);
        }

        for (UpdateFeatureTask flagToUpdate : actions.flagsToUpdate()) {
            unleashStateUpdater.updateFeature(flagToUpdate);
        }

        for (DeleteFeatureTask flagToDelete : actions.flagsToDelete()) {
            unleashStateUpdater.deleteFeature(flagToDelete);
        }


        //TODO создание тэгов
        //TODO удаление тэгов
        //TODO обновление тэгов у флагов через отдельную ручку
    }
}
