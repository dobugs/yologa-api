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
        this.value = value;
    }
}
