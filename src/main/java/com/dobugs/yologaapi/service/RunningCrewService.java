package com.dobugs.yologaapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dobugs.yologaapi.domain.runningcrew.Capacity;
import com.dobugs.yologaapi.domain.runningcrew.Coordinates;
import com.dobugs.yologaapi.domain.runningcrew.Deadline;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.service.dto.common.CoordinatesDto;
import com.dobugs.yologaapi.service.dto.common.DateDto;
import com.dobugs.yologaapi.service.dto.common.LocationsDto;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewFindNearbyRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewFindNearbyResponse;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;
import com.dobugs.yologaapi.support.TokenGenerator;
import com.dobugs.yologaapi.support.dto.response.UserTokenResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class RunningCrewService {

    private final RunningCrewRepository runningCrewRepository;
    private final TokenGenerator tokenGenerator;

    public long create(final String serviceToken, final RunningCrewCreateRequest request) {
        final UserTokenResponse userTokenResponse = tokenGenerator.extract(serviceToken);
        final Long memberId = userTokenResponse.memberId();

        final RunningCrew runningCrew = convertToRunningCrew(memberId, request);
        final RunningCrew savedRunningCrew = runningCrewRepository.save(runningCrew);

        return savedRunningCrew.getId();
    }

    @Transactional(readOnly = true)
    public RunningCrewFindNearbyResponse findNearby(final RunningCrewFindNearbyRequest request) {
        final Pageable pageable = PageRequest.of(request.page(), request.size());
        final Page<RunningCrew> runningCrews = runningCrewRepository.findNearby(
            request.latitude(), request.longitude(), request.radius(), pageable
        );
        return RunningCrewFindNearbyResponse.from(runningCrews);
    }

    @Transactional(readOnly = true)
    public RunningCrewResponse findById(final Long runningCrewId) {
        final RunningCrew runningCrew = findRunningCrewBy(runningCrewId);
        return RunningCrewResponse.from(runningCrew);
    }

    public void update(final String serviceToken, final Long runningCrewId, final RunningCrewUpdateRequest request) {
        final UserTokenResponse userTokenResponse = tokenGenerator.extract(serviceToken);
        final Long memberId = userTokenResponse.memberId();
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        update(memberId, request, savedRunningCrew);
    }

    public void delete(final String serviceToken, final Long runningCrewId) {
        final UserTokenResponse userTokenResponse = tokenGenerator.extract(serviceToken);
        final Long memberId = userTokenResponse.memberId();
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.delete(memberId);
    }

    public void start(final Long runningCrewId) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.start();
    }

    public void end(final Long runningCrewId) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.end();
    }

    private RunningCrew findRunningCrewBy(final Long runningCrewId) {
        return runningCrewRepository.findByIdAndArchived(runningCrewId, true)
            .orElseThrow(() -> new IllegalArgumentException(String.format("러닝크루가 존재하지 않습니다. [%d]", runningCrewId)));
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
