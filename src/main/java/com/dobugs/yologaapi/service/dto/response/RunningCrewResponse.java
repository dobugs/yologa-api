package com.dobugs.yologaapi.service.dto.response;

import java.time.LocalDateTime;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.service.dto.common.DatesDto;
import com.dobugs.yologaapi.service.dto.common.LocationsDto;

public record RunningCrewResponse(Long id, String title, Long host, LocationsDto location, String status,
                                  int capacity, int numberOfParticipants, DatesDto date, LocalDateTime deadline,
                                  String description) {

    public static RunningCrewResponse from(final RunningCrew runningCrew) {
        return new RunningCrewResponse(
            runningCrew.getId(),
            runningCrew.getTitle(),
            runningCrew.getMemberId(),
            LocationsDto.from(runningCrew.getDeparture(), runningCrew.getArrival()),
            runningCrew.getStatus().name(),
            runningCrew.getCapacity().getValue(),
            runningCrew.getNumberOfParticipants(),
            DatesDto.from(
                runningCrew.getScheduledStartDate(), runningCrew.getScheduledEndDate(),
                runningCrew.getImplementedStartDate(), runningCrew.getImplementedEndDate()
            ),
            runningCrew.getDeadline().getValue(),
            runningCrew.getDescription()
        );
    }
}
