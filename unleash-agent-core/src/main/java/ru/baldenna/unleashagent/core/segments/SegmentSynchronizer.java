package ru.baldenna.unleashagent.core.segments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.auth.UnleashSessionManager;
import ru.baldenna.unleashagent.core.client.UnleashClient;
import ru.baldenna.unleashagent.core.configuration.UnleashConfiguration;
import ru.baldenna.unleashagent.core.segments.model.CreateSegmentRequest;
import ru.baldenna.unleashagent.core.segments.model.Segment;
import ru.baldenna.unleashagent.core.segments.model.UpdateSegmentRequest;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class SegmentSynchronizer {

    private final UnleashClient unleashClient;
    private final UnleashSessionManager unleashSessionManager;

    public void synchronize(UnleashConfiguration newConfiguration) {
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
                        log.info("Segment {} with name {} needs to be updated", localSegment.name(), localSegment.name());
                        segmentsToUpdate.add(new Segment(localSegment, existingSegment.id()));
                    } else {
                        log.debug("Segment {} with name {} already exists and is up to date", localSegment.name(), localSegment.name());
                    }
                } else {
                    log.info("Segment {} with name {} not found in Unleash and needs to be created", localSegment.name(), localSegment.name());
                    segmentsToCreate.add(localSegment);
                }
            }

            for (Segment remoteSegment : remoteSegments) {
                if (localSegments.stream().noneMatch(localSegment -> localSegment.name().equals(remoteSegment.name()))) {
                    log.info("Segment {} with name {} exists in Unleash but not declared in local config. Segment will be deleted", remoteSegment.name(), remoteSegment.name());
                    segmentsToDelete.add(remoteSegment);
                }
            }

            if (segmentsToCreate.size() + segmentsToUpdate.size() + segmentsToDelete.size() != 0) {
                log.info("Segment states were compared. Count to create = {}, count to update = {}, count to delete = {}",
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
        }
    }

    private boolean isSegmentEquals(Segment localSegment, Segment remoteSegment) {
        var segmentEquals = true;
        // Compare name
        if (notEquals(localSegment.name(),(remoteSegment.name()))) {
            log.info("Segments differ in 'name' field: local={}, remote={}", localSegment.name(), remoteSegment.name());
            segmentEquals = false;
        }

        // Compare description
        if (notEquals(localSegment.description(),(remoteSegment.description()))) {
            log.info("Segments differ in 'description' field: local={}, remote={}", localSegment.description(), remoteSegment.description());
            segmentEquals = false;
        }

        // Compare project
        if (notEquals(localSegment.project(),(remoteSegment.project()))) {
            log.info("Segments differ in 'project' field: local={}, remote={}", localSegment.project(), remoteSegment.project());
            segmentEquals = false;
        }

        // Compare constraints
        var localConstraints = localSegment.constraints();
        var remoteConstraints = remoteSegment.constraints();
        if (localConstraints.size() != remoteConstraints.size()) {
            log.info("Segments differ in 'constraints' count: local={}, remote={}", localConstraints.size(), remoteConstraints.size());
            return false;
        }
        for (int i = 0; i < localConstraints.size(); i++) {
            var localConstraint = localConstraints.get(i);
            var remoteConstraint = remoteConstraints.get(i);
            if (notEquals(localConstraint, (remoteConstraint))) {
                log.info("Segments differ in 'constraints' at index {}: local={}, remote={}", i, localConstraint, remoteConstraint);
                segmentEquals = false;
            }
        }

        return segmentEquals;
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

    private boolean notEquals(Object local, Object remote) {
        return !equals(local,remote);
    }

    private void createSegment(Segment segment) {
        unleashClient.createSegment(
                new CreateSegmentRequest(segment.name(), segment.description(), segment.project(), segment.constraints()),
                unleashSessionManager.getSessionCookie() );
    }

    private void updateSegment(Segment segment) {
        unleashClient.updateSegment(
                segment.id(),
                new UpdateSegmentRequest(segment.name(), segment.description(), segment.project(), segment.constraints()),
                unleashSessionManager.getSessionCookie());
    }

    private void deleteSegment(Segment segment) {
        unleashClient.deleteSegment(segment.id(), unleashSessionManager.getSessionCookie());
    }
}
