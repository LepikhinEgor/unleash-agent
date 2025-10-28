package ru.alfabank.dfa.unleash.agent.segments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.dfa.unleash.agent.auth.UnleashSessionManager;
import ru.alfabank.dfa.unleash.agent.client.UnleashClient;
import ru.alfabank.dfa.unleash.agent.configuration.UnleashConfiguration;
import ru.alfabank.dfa.unleash.agent.segments.model.CreateSegmentRequest;
import ru.alfabank.dfa.unleash.agent.segments.model.Segment;
import ru.alfabank.dfa.unleash.agent.segments.model.UpdateSegmentRequest;

import java.util.ArrayList;

import static ru.alfabank.dfa.unleash.agent.utils.CompareUtils.deepCompare;

@Slf4j
@RequiredArgsConstructor
public class SegmentSynchronizer {

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    public boolean synchronize(UnleashConfiguration newConfiguration) {
        try {
            log.info("Check unleash segments for update");
            var remoteSegments = unleashClient.getSegments(unleashSessionManager.getSessionCookie())
                    .segments();
            var localSegments = newConfiguration.segments();

            var segmentsToCreate = new ArrayList<Segment>();
            var segmentsToUpdate = new ArrayList<Segment>();
            var segmentsToDelete = new ArrayList<Segment>();

            for (Segment localSegment : localSegments) {
                var segmentAlreadyExists = remoteSegments.stream()
                        .anyMatch(remoteSegment -> remoteSegment.name().equals(localSegment.name()));
                if (segmentAlreadyExists) {
                    var existingSegment = remoteSegments.stream()
                            .filter(remoteSegment -> remoteSegment.name().equals(localSegment.name()))
                            .findFirst()
                            .orElseThrow();
                    if (!isSegmentEquals(localSegment, existingSegment)) {
                        log.info("Segment {} with name {} needs to be updated",
                                localSegment.name(), localSegment.name());
                        segmentsToUpdate.add(localSegment.copyWithId(existingSegment.id()));
                    } else {
                        log.debug("Segment {} with name {} already exists and is up to date",
                                localSegment.name(), localSegment.name());
                    }
                } else {
                    log.info("Segment {} with name {} not found in Unleash and needs to be created",
                            localSegment.name(), localSegment.name());
                    segmentsToCreate.add(localSegment);
                }
            }

            for (Segment remoteSegment : remoteSegments) {
                if (localSegments.stream().noneMatch(local -> local.name().equals(remoteSegment.name()))) {
                    log.info("Segment {} with name {} exists in Unleash but not declared in local config."
                            + " Segment will be deleted", remoteSegment.name(), remoteSegment.name());
                    segmentsToDelete.add(remoteSegment);
                }
            }

            if (segmentsToCreate.size() + segmentsToUpdate.size() + segmentsToDelete.size() != 0) {
                log.info("Segment states were compared. To create = {}, to update = {}, to delete = {}",
                        segmentsToCreate.size(), segmentsToUpdate.size(), segmentsToDelete.size());
            } else {
                log.info("Unleash segments are already up to date");
            }

            segmentsToCreate.forEach(this::createSegment);
            segmentsToUpdate.forEach(this::updateSegment);
            segmentsToDelete.forEach(this::deleteSegment);
        } catch (Exception e) {
            log.warn("Error while segments synchronization", e);
            log.debug(e.getMessage(), e);

            return false;
        }

        return true;
    }

    private boolean isSegmentEquals(Segment localSegment, Segment remoteSegment) {
        return deepCompare(localSegment.copyWithId(remoteSegment.id()), remoteSegment);
    }

    private boolean equals(Object local, Object remote) {
        if (local == null && remote == null) {
            return true;
        }
        if (local == null || remote == null) {
            return false;
        }

        return local.equals(remote);
    }


    private void createSegment(Segment segment) {
        unleashClient.createSegment(
                new CreateSegmentRequest(
                        segment.name(),
                        segment.description(),
                        segment.project(),
                        segment.constraints()
                ),
                unleashSessionManager.getSessionCookie());
    }

    private void updateSegment(Segment segment) {
        unleashClient.updateSegment(
                segment.id(),
                new UpdateSegmentRequest(
                        segment.name(),
                        segment.description(),
                        segment.project(),
                        segment.constraints()
                ),
                unleashSessionManager.getSessionCookie());
    }

    private void deleteSegment(Segment segment) {
        unleashClient.deleteSegment(segment.id(), unleashSessionManager.getSessionCookie());
    }
}
