package com.dobugs.yologaapi.service.dto.common;

import java.time.LocalDateTime;

public class DateDto {

    private LocalDateTime start;
    private LocalDateTime end;

    private DateDto() {
    }

    public DateDto(final LocalDateTime start, final LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
