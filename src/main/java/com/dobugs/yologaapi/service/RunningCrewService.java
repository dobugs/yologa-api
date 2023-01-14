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
import com.dobugs.yologaapi.service.dto.request.PageDto;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewFindNearbyRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewFindNearbyResponse;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class RunningCrewService {

    private final RunningCrewRepository runningCrewRepository;

    public long create(final RunningCrewCreateRequest request) {
        final RunningCrew runningCrew = convertToRunningCrew(request);
        final RunningCrew savedRunningCrew = runningCrewRepository.save(runningCrew);

        return savedRunningCrew.getId();
    }

    @Transactional(readOnly = true)
    public RunningCrewFindNearbyResponse findNearby(final RunningCrewFindNearbyRequest request) {
        final PageDto pageDto = request.pageDto();
        final Pageable pageable = PageRequest.of(pageDto.getPage(), pageDto.getSize());
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

    public void update(final Long runningCrewId, final RunningCrewUpdateRequest request) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);

        update(request, savedRunningCrew);
    }

    public void delete(final Long runningCrewId) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);

        savedRunningCrew.delete();
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

    private void update(final RunningCrewUpdateRequest request, final RunningCrew savedRunningCrew) {
        final LocationsDto locationsDto = request.location();
        final DateDto dateDto = request.date();

        savedRunningCrew.update(
            convertToCoordinates(locationsDto.getDeparture()), convertToCoordinates(locationsDto.getArrival()),
            new Capacity(request.capacity()),
            dateDto.getStart(), dateDto.getEnd(),
            new Deadline(request.deadline()),
            request.title(), request.description()
        );
    }

    private RunningCrew convertToRunningCrew(final RunningCrewCreateRequest request) {
        final long memberId = 1L;

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
