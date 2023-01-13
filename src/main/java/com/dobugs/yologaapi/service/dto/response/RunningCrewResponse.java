package com.dobugs.yologaapi.service.dto.response;

import java.time.LocalDateTime;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.service.dto.common.DatesDto;
import com.dobugs.yologaapi.service.dto.common.LocationsDto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RunningCrewResponse {

    private final Long id;
    private final String title;
    private final Long host;
    private final LocationsDto locationsDto;
    private final String status;
    private final int capacity;
    private final DatesDto date;
    private final LocalDateTime deadline;
    private final String description;

    public static RunningCrewResponse from(final RunningCrew runningCrew) {
        return new RunningCrewResponse(
            runningCrew.getId(),
            runningCrew.getTitle(),
            runningCrew.getMemberId(),
            LocationsDto.from(runningCrew.getDeparture(), runningCrew.getArrival()),
            runningCrew.getStatus().name(),
            runningCrew.getCapacity().getValue(),
            DatesDto.from(
                runningCrew.getScheduledStartDate(), runningCrew.getScheduledEndDate(),
                runningCrew.getImplementedStartDate(), runningCrew.getImplementedEndDate()
            ),
            runningCrew.getDeadline().getValue(),
            runningCrew.getDescription()
        );
    }
}
