package com.dobugs.yologaapi.service.dto.common;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DateDto {

    private LocalDateTime start;
    private LocalDateTime end;

    public DateDto(final LocalDateTime start, final LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
}
