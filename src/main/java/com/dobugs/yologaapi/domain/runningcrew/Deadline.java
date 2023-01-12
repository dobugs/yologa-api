package com.dobugs.yologaapi.domain.runningcrew;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Deadline {

    @Column(name = "deadline", nullable = false)
    private LocalDateTime value;

    protected Deadline() {
    }

    public Deadline(final LocalDateTime value) {
        validateDeadlineIsAfterThanNow(value);
        this.value = value;
    }

    private void validateDeadlineIsAfterThanNow(final LocalDateTime value) {
        if (value.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(String.format("마감 기한이 현재 시간보다 이전입니다. [%s]", value));
        }
    }
}
