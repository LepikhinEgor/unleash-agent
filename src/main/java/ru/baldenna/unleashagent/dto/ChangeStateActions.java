package ru.baldenna.unleashagent.dto;

import ru.baldenna.unleashagent.task.CreateFeatureTask;
import ru.baldenna.unleashagent.task.DeleteFeatureTask;
import ru.baldenna.unleashagent.task.UpdateFeatureTask;

import java.util.List;

public record ChangeStateActions (
     List<CreateFeatureTask> flagsToCreate,
     List<UpdateFeatureTask> flagsToUpdate,
     List<DeleteFeatureTask> flagsToDelete
) {}
