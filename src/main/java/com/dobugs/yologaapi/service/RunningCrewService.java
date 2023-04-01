package com.dobugs.yologaapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dobugs.yologaapi.auth.dto.response.ServiceToken;
import com.dobugs.yologaapi.domain.runningcrew.Capacity;
import com.dobugs.yologaapi.domain.runningcrew.Coordinates;
import com.dobugs.yologaapi.domain.runningcrew.Deadline;
import com.dobugs.yologaapi.domain.runningcrew.ParticipantType;
import com.dobugs.yologaapi.domain.runningcrew.ProgressionType;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.service.dto.common.CoordinatesDto;
import com.dobugs.yologaapi.service.dto.common.DateDto;
import com.dobugs.yologaapi.service.dto.common.LocationsDto;
import com.dobugs.yologaapi.service.dto.request.PagingRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewFindNearbyRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewStatusRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewFindNearbyResponse;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;
import com.dobugs.yologaapi.service.dto.response.RunningCrewsResponse;
import com.dobugs.yologaapi.support.PagingGenerator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class RunningCrewService {

    private final RunningCrewRepository runningCrewRepository;
    private final PagingGenerator pagingGenerator;

    public long create(final ServiceToken serviceToken, final RunningCrewCreateRequest request) {
        final RunningCrew runningCrew = convertToRunningCrew(serviceToken.memberId(), request);
        final RunningCrew savedRunningCrew = runningCrewRepository.save(runningCrew);

        return savedRunningCrew.getId();
    }

    @Transactional(readOnly = true)
    public RunningCrewFindNearbyResponse findNearby(final RunningCrewFindNearbyRequest request) {
        final Pageable pageable = pagingGenerator.from(request.page(), request.size());
        final Page<RunningCrew> runningCrews = runningCrewRepository.findNearby(
            request.latitude(), request.longitude(), request.radius(), pageable
        );
        return RunningCrewFindNearbyResponse.from(runningCrews);
    }

    @Transactional(readOnly = true)
    public RunningCrewsResponse findInProgress(final ServiceToken serviceToken, final PagingRequest request) {
        final String runningCrewStatus = ProgressionType.IN_PROGRESS.getSavedName();
        final String participantStatus = ParticipantType.PARTICIPATING.getSavedName();
        final Pageable pageable = pagingGenerator.from(request.page(), request.size());
        final Page<RunningCrew> runningCrews = runningCrewRepository.findInProgress(serviceToken.memberId(), runningCrewStatus, participantStatus, pageable);
        return RunningCrewsResponse.from(runningCrews);
    }

    @Transactional(readOnly = true)
    public RunningCrewsResponse findHosted(final ServiceToken serviceToken, final RunningCrewStatusRequest request) {
        final ProgressionType progressionType = selectProgressionType(request.status());
        final Pageable pageable = pagingGenerator.from(request.page(), request.size());
        final Page<RunningCrew> runningCrews = findHostedRunningCrews(serviceToken.memberId(), progressionType, pageable);
        return RunningCrewsResponse.from(runningCrews);
    }

    @Transactional(readOnly = true)
    public RunningCrewsResponse findParticipated(final ServiceToken serviceToken, final RunningCrewStatusRequest request) {
        final ProgressionType progressionType = selectProgressionType(request.status());
        final Pageable pageable = pagingGenerator.from(request.page(), request.size());
        final Page<RunningCrew> runningCrews = findParticipatedRunningCrews(serviceToken.memberId(), progressionType, pageable);
        return RunningCrewsResponse.from(runningCrews);
    }

    @Transactional(readOnly = true)
    public RunningCrewResponse findById(final Long runningCrewId) {
        final RunningCrew runningCrew = findRunningCrewBy(runningCrewId);
        return RunningCrewResponse.from(runningCrew);
    }

    public void update(final ServiceToken serviceToken, final Long runningCrewId, final RunningCrewUpdateRequest request) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        update(serviceToken.memberId(), request, savedRunningCrew);
    }

    public void delete(final ServiceToken serviceToken, final Long runningCrewId) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.delete(serviceToken.memberId());
    }

    public void start(final ServiceToken serviceToken, final Long runningCrewId) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.start(serviceToken.memberId());
    }

    public void end(final ServiceToken serviceToken, final Long runningCrewId) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.end(serviceToken.memberId());
    }

    private RunningCrew findRunningCrewBy(final Long runningCrewId) {
        return runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("러닝크루가 존재하지 않습니다. [%d]", runningCrewId)));
    }

    private Page<RunningCrew> findHostedRunningCrews(final Long memberId, final ProgressionType progressionType, final Pageable pageable) {
        if (progressionType == null) {
            return runningCrewRepository.findByMemberIdAndArchivedIsTrue(memberId, pageable);
        }
        return runningCrewRepository.findByMemberIdAndStatusAndArchivedIsTrue(memberId, progressionType, pageable);
    }

    private Page<RunningCrew> findParticipatedRunningCrews(final Long memberId, final ProgressionType progressionType, final Pageable pageable) {
        final String participating = ParticipantType.PARTICIPATING.getSavedName();
        if (progressionType == null) {
            return runningCrewRepository.findParticipated(memberId, participating, pageable);
        }
        return runningCrewRepository.findParticipatedByStatus(memberId, progressionType.getSavedName(), participating, pageable);
    }

    private ProgressionType selectProgressionType(final String status) {
        if (status == null) {
            return null;
        }
        return ProgressionType.from(status);
    }

    private void update(final Long memberId, final RunningCrewUpdateRequest request, final RunningCrew savedRunningCrew) {
        final LocationsDto locationsDto = request.location();
        final DateDto dateDto = request.date();

        savedRunningCrew.update(
            memberId,
            convertToCoordinates(locationsDto.getDeparture()), convertToCoordinates(locationsDto.getArrival()),
            new Capacity(request.capacity()),
            dateDto.getStart(), dateDto.getEnd(),
            new Deadline(request.deadline()),
            request.title(), request.description()
        );
    }

    private RunningCrew convertToRunningCrew(final Long memberId, final RunningCrewCreateRequest request) {
        final LocationsDto locationsDto = request.location();
        final Coordinates departure = convertToCoordinates(locationsDto.getDeparture());
        final Coordinates arrival = convertToCoordinates(locationsDto.getArrival());

        final DateDto dateDto = request.date();
        return new RunningCrew(memberId, departure, arrival, new Capacity(request.capacity()),
            dateDto.getStart(), dateDto.getEnd(), new Deadline(request.deadline()),
            request.title(), request.description());
    }

    private Coordinates convertToCoordinates(final CoordinatesDto coordinatesDto) {
        return new Coordinates(coordinatesDto.getLatitude(), coordinatesDto.getLongitude());
    }
}
