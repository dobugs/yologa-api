package com.dobugs.yologaapi.service.dto.common;

import java.time.LocalDateTime;

public class DatesDto {

    private DateDto scheduled;
    private DateDto implemented;

    private DatesDto() {
    }

    public DatesDto(final DateDto scheduled, final DateDto implemented) {
        this.scheduled = scheduled;
        this.implemented = implemented;
    }

    public static DatesDto from(
        final LocalDateTime scheduledStartDate,
        final LocalDateTime scheduledEndDate,
        final LocalDateTime implementedStartDate,
        final LocalDateTime implementedEndDate
    ) {
        return new DatesDto(
            new DateDto(scheduledStartDate, scheduledEndDate),
            new DateDto(implementedStartDate, implementedEndDate)
        );
    }

    public DateDto getScheduled() {
        return scheduled;
    }

    public DateDto getImplemented() {
        return implemented;
    }
}
