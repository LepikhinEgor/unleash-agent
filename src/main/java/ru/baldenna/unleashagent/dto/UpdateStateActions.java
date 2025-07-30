package ru.baldenna.unleashagent.dto;

import ru.baldenna.unleashagent.task.CreateFeatureTask;
import ru.baldenna.unleashagent.task.CreateTagTask;
import ru.baldenna.unleashagent.task.DeleteFeatureTask;
import ru.baldenna.unleashagent.task.DeleteTagTask;
import ru.baldenna.unleashagent.task.UpdateFeatureTask;

import java.util.ArrayList;
import java.util.List;

public record UpdateStateActions(
     List<CreateTagTask> tagsToCreate,
     List<DeleteTagTask> tagsToDelete,
     List<CreateFeatureTask> flagsToCreate,
     List<UpdateFeatureTask> flagsToUpdate,
     List<DeleteFeatureTask> flagsToDelete
) {}
