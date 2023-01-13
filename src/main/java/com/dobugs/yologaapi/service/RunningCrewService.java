package com.dobugs.yologaapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dobugs.yologaapi.domain.runningcrew.Capacity;
import com.dobugs.yologaapi.domain.runningcrew.Coordinates;
import com.dobugs.yologaapi.domain.runningcrew.Deadline;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.service.dto.common.CoordinatesDto;
import com.dobugs.yologaapi.service.dto.common.DateDto;
import com.dobugs.yologaapi.service.dto.common.LocationDto;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;

@Transactional
@Service
public class RunningCrewService {

    private final RunningCrewRepository runningCrewRepository;

    public RunningCrewService(final RunningCrewRepository runningCrewRepository) {
        this.runningCrewRepository = runningCrewRepository;
    }

    public long create(final RunningCrewCreateRequest request) {
        final RunningCrew runningCrew = convertToRunningCrew(request);
        final RunningCrew savedRunningCrew = runningCrewRepository.save(runningCrew);

        return savedRunningCrew.getId();
    }

    private RunningCrew convertToRunningCrew(final RunningCrewCreateRequest request) {
        final long memberId = 1L;

        final LocationDto locationDto = request.location();
        final Coordinates departure = convertToCoordinates(locationDto.getDeparture());
        final Coordinates arrival = convertToCoordinates(locationDto.getArrival());

        final DateDto dateDto = request.date();
        return new RunningCrew(memberId, departure, arrival, new Capacity(request.capacity()),
            dateDto.getStart(), dateDto.getEnd(), new Deadline(request.deadline()),
            request.title(), request.description());
    }

    private Coordinates convertToCoordinates(final CoordinatesDto coordinatesDto) {
        return new Coordinates(coordinatesDto.getLatitude(), coordinatesDto.getLongitude());
    }
}
