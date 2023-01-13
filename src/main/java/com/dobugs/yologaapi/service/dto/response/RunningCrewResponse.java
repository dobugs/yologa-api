package com.dobugs.yologaapi.service.dto.response;

import java.time.LocalDateTime;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.service.dto.common.DatesDto;
import com.dobugs.yologaapi.service.dto.common.LocationsDto;

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

    private RunningCrewResponse(
        final Long id, final String title, final Long host, final LocationsDto locationsDto, final String status,
        final int capacity, final DatesDto date, final LocalDateTime deadline, final String description
    ) {
        this.id = id;
        this.title = title;
        this.host = host;
        this.locationsDto = locationsDto;
        this.status = status;
        this.capacity = capacity;
        this.date = date;
        this.deadline = deadline;
        this.description = description;
    }

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

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getHost() {
        return host;
    }

    public LocationsDto getLocationsDto() {
        return locationsDto;
    }

    public String getStatus() {
        return status;
    }

    public int getCapacity() {
        return capacity;
    }

    public DatesDto getDate() {
        return date;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public String getDescription() {
        return description;
    }
}
