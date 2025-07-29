package ru.baldenna.unleashagent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.baldenna.unleashagent.dto.ChangeStateActions;
import ru.baldenna.unleashagent.dto.CompareResult;
import ru.baldenna.unleashagent.dto.CompareResultType;
import ru.baldenna.unleashagent.dto.ConfigurationState;
import ru.baldenna.unleashagent.dto.Feature;
import ru.baldenna.unleashagent.task.CreateFeatureTask;
import ru.baldenna.unleashagent.task.DeleteFeatureTask;
import ru.baldenna.unleashagent.task.UpdateFeatureTask;

import java.util.ArrayList;

import static ru.baldenna.unleashagent.dto.CompareResultType.CHANGED;
import static ru.baldenna.unleashagent.dto.CompareResultType.EQUAL;
import static ru.baldenna.unleashagent.dto.CompareResultType.ERROR;
import static ru.baldenna.unleashagent.dto.CompareResultType.NOT_EQUAL;


@Slf4j
@Service
public class StateComparator {

    public ChangeStateActions compare(ConfigurationState local, ConfigurationState remote) {
        var flagsToCreate = new ArrayList<CreateFeatureTask>();
        var flagsToUpdate = new ArrayList<UpdateFeatureTask>();
        var flagsToDelete = new ArrayList<DeleteFeatureTask>();
        for (Feature localFlag : local.features()) {
            var featureAlreadyActual = remote.features().stream().
                    map((remoteFeature) -> compareFeatures(localFlag,remoteFeature) )
                    .anyMatch(compareResult -> compareResult.type() == EQUAL);
            if (featureAlreadyActual) {
                log.debug("Feature {} already exists and actual", localFlag.name());
                continue;
            }

            var featureChanged = remote.features().stream().
                    map((remoteFeature) -> compareFeatures(localFlag,remoteFeature) )
                    .filter(compareResult -> compareResult.type() == CHANGED)
                    .findFirst();
            if (featureChanged.isPresent()) {
                log.info("Feature {} exists but need to be changed. Reason: {}", localFlag.name(), featureChanged.get().details());
                flagsToUpdate.add(new UpdateFeatureTask(localFlag.name(),localFlag.type(), localFlag.description()));
                continue;
            }

            log.info("Feature {} not found in Unleash and need to be created", localFlag.name());
            flagsToCreate.add(new CreateFeatureTask(localFlag.name(), localFlag.type(), localFlag.description(), localFlag.tags()));
        }
        for (Feature remoteFlag : remote.features()) {
            if (local.features().stream().noneMatch(localFlag -> localFlag.name().equals(remoteFlag.name()))) {
                log.info("Feature {} exists in Unleash but not declared in local config. Feature will be deleted", remoteFlag.name());
                flagsToDelete.add(new DeleteFeatureTask(remoteFlag.name()));
            }
        }

        if (flagsToCreate.size() + flagsToUpdate.size() + flagsToDelete.size() != 0) {
            log.info("Configuration states was compared. Count to create = {}, count to update = {}, count to delete = {}", flagsToCreate.size(), flagsToUpdate.size(), flagsToDelete.size());
        } else {
            log.info("Unleash configuration already up to date");
        }

        return new ChangeStateActions(flagsToCreate, flagsToUpdate,flagsToDelete);
    }

    private CompareResult compareFeatures(Feature local, Feature remote) {
        try {
            boolean nameEquals = local.name().equals(remote.name());
            if (!nameEquals) {
                return new CompareResult(NOT_EQUAL, "Different features: " + local.name() + " and " + remote.name());
            }
            boolean typeEquals = local.type().equals(remote.type());
            if (!typeEquals) {
                return new CompareResult(CompareResultType.CHANGED, "Feature " + remote.name() + " type changed: " + remote.type() + " -> " + local.type());
            }
            boolean descriptionEquals = local.description().equals(remote.description());
            if (!descriptionEquals) {
                return new CompareResult(CompareResultType.CHANGED, "Feature " + remote.name() + " description changed: " + remote.description() + " -> " + local.description());
            }
            return new CompareResult(CompareResultType.EQUAL, "Feature " + remote.name() + " has actual state");
        } catch (Exception e) {
            log.error("Error trying compare features", e);
            return new CompareResult(ERROR, e.getMessage());
        }
    }
}
