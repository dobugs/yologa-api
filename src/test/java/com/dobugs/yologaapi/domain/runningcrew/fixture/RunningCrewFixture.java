package com.dobugs.yologaapi.domain.runningcrew.fixture;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;

import com.dobugs.yologaapi.domain.runningcrew.Capacity;
import com.dobugs.yologaapi.domain.runningcrew.Coordinates;
import com.dobugs.yologaapi.domain.runningcrew.Deadline;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrewProgression;
import com.dobugs.yologaapi.service.dto.common.CoordinatesDto;
import com.dobugs.yologaapi.service.dto.common.DateDto;
import com.dobugs.yologaapi.service.dto.common.DatesDto;
import com.dobugs.yologaapi.service.dto.common.LocationDto;
import com.dobugs.yologaapi.service.dto.common.LocationsDto;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;
import com.dobugs.yologaapi.service.dto.response.RunningCrewSummaryResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RunningCrewFixture {

    public static final double LATITUDE = 37.530160973856;
    public static final double LONGITUDE = 127.12379233466;
    public static final Coordinates COORDINATES = new Coordinates(LATITUDE, LONGITUDE);

    public static final String RUNNING_CREW_TITLE = "title";
    public static final int RUNNING_CREW_CAPACITY = 10;
    public static final String RUNNING_CREW_DESCRIPTION = "description";

    public static final LocalDateTime NOW = LocalDateTime.now();
    public static final LocalDateTime AFTER_ONE_HOUR = LocalDateTime.now().plusHours(1);
    public static final DateDto DATE_DTO = new DateDto(NOW, AFTER_ONE_HOUR);
    public static final DateDto NOT_IMPLEMENTED_DATE_DTO = new DateDto(null, null);
    public static final DatesDto DATES_DTO = new DatesDto(DATE_DTO, NOT_IMPLEMENTED_DATE_DTO);

    public static final CoordinatesDto COORDINATES_DTO = new CoordinatesDto(LATITUDE, LONGITUDE);
    public static final LocationDto LOCATION_DTO = new LocationDto(COORDINATES_DTO);
    public static final LocationsDto LOCATIONS_DTO = new LocationsDto(COORDINATES_DTO, COORDINATES_DTO);

    private static RunningCrew createRunningCrew(
        final Long memberId, final Coordinates departure, final Coordinates arrival,
        final int capacity, final LocalDateTime scheduledStartDate, final LocalDateTime scheduledEndDate,
        final LocalDateTime deadline, final String title, final String description
    ) {
        return new RunningCrewBuilder()
            .memberId(memberId)
            .departure(departure)
            .arrival(arrival)
            .capacity(new Capacity(capacity))
            .scheduledStartDate(scheduledStartDate)
            .scheduledEndDate(scheduledEndDate)
            .deadline(new Deadline(deadline))
            .title(title)
            .description(description)
            .build();
    }

    public static RunningCrew createRunningCrew() {
        return createRunningCrew(
            1L, COORDINATES, COORDINATES, RUNNING_CREW_CAPACITY,
            NOW, AFTER_ONE_HOUR, AFTER_ONE_HOUR,
            RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
        );
    }

    public static RunningCrew createRunningCrewWith(final LocalDateTime start, final LocalDateTime end) {
        return createRunningCrew(
            1L, COORDINATES, COORDINATES, RUNNING_CREW_CAPACITY,
            start, end, AFTER_ONE_HOUR,
            RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
        );
    }

    public static RunningCrewCreateRequest createRunningCrewCreateRequest() {
        return new RunningCrewCreateRequest(
            RUNNING_CREW_TITLE, LOCATIONS_DTO, RUNNING_CREW_CAPACITY, DATE_DTO, AFTER_ONE_HOUR, RUNNING_CREW_DESCRIPTION
        );
    }

    public static RunningCrewSummaryResponse createRunningCrewSummaryResponse() {
        return new RunningCrewSummaryResponse(
            1L, LOCATION_DTO, RunningCrewProgression.CREATED.name(), LocalDateTime.now()
        );
    }

    public static RunningCrewUpdateRequest createRunningCrewUpdateRequest() {
        return new RunningCrewUpdateRequest(
            RUNNING_CREW_TITLE, LOCATIONS_DTO, RUNNING_CREW_CAPACITY, DATE_DTO, AFTER_ONE_HOUR, RUNNING_CREW_DESCRIPTION
        );
    }

    public static RunningCrewResponse createRunningCrewResponse(final Long runningCrewId) {
        return new RunningCrewResponse(
            runningCrewId, RUNNING_CREW_TITLE, 1L, LOCATIONS_DTO, RunningCrewProgression.CREATED.name(),
            RUNNING_CREW_CAPACITY, DATES_DTO, AFTER_ONE_HOUR, RUNNING_CREW_DESCRIPTION
        );
    }

    public static RunningCrew createMockRunningCrew() {
        final Point point = mock(Point.class);
        given(point.getX()).willReturn(LATITUDE);
        given(point.getY()).willReturn(LONGITUDE);

        final RunningCrew runningCrew = mock(RunningCrew.class);
        given(runningCrew.getDeparture()).willReturn(point);
        given(runningCrew.getArrival()).willReturn(point);
        given(runningCrew.getStatus()).willReturn(RunningCrewProgression.CREATED);
        given(runningCrew.getCapacity()).willReturn(new Capacity(RUNNING_CREW_CAPACITY));
        given(runningCrew.getDeadline()).willReturn(new Deadline(AFTER_ONE_HOUR));

        return runningCrew;
    }

    public static class RunningCrewBuilder {

        private Long memberId;
        private Coordinates departure;
        private Coordinates arrival;
        private Capacity capacity;
        private LocalDateTime scheduledStartDate;
        private LocalDateTime scheduledEndDate;
        private Deadline deadline;
        private String title;
        private String description;

        public RunningCrewBuilder() {
        }

        public RunningCrew build() {
            return new RunningCrew(
                memberId, departure, arrival, capacity,
                scheduledStartDate, scheduledEndDate, deadline, title, description
            );
        }

        public RunningCrewBuilder memberId(final Long memberId) {
            this.memberId = memberId;
            return this;
        }

        public RunningCrewBuilder departure(final Coordinates departure) {
            this.departure = departure;
            return this;
        }

        public RunningCrewBuilder arrival(final Coordinates arrival) {
            this.arrival = arrival;
            return this;
        }

        public RunningCrewBuilder capacity(final Capacity capacity) {
            this.capacity = capacity;
            return this;
        }

        public RunningCrewBuilder scheduledStartDate(final LocalDateTime scheduledStartDate) {
            this.scheduledStartDate = scheduledStartDate;
            return this;
        }

        public RunningCrewBuilder scheduledEndDate(final LocalDateTime scheduledEndDate) {
            this.scheduledEndDate = scheduledEndDate;
            return this;
        }

        public RunningCrewBuilder deadline(final Deadline deadline) {
            this.deadline = deadline;
            return this;
        }

        public RunningCrewBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public  RunningCrewBuilder description(final String description) {
            this.description = description;
            return this;
        }
    }
}
