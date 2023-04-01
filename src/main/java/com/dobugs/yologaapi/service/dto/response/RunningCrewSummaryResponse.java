package com.dobugs.yologaapi.service.dto.response;

import java.time.LocalDateTime;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.service.dto.common.LocationDto;

public record RunningCrewSummaryResponse(Long id, LocationDto location, String status, LocalDateTime deadline) {

    public static RunningCrewSummaryResponse from(final RunningCrew runningCrew) {
        return new RunningCrewSummaryResponse(
            runningCrew.getId(),
            LocationDto.from(runningCrew.getDeparture()),
            runningCrew.getStatus().name(),
            runningCrew.getDeadline().getValue()
        );
    }
}
