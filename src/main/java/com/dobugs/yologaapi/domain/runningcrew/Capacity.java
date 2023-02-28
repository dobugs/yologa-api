package com.dobugs.yologaapi.domain.runningcrew;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Capacity {

    private static final int MINIMUM = 2;

    @Column(name = "capacity", nullable = false)
    private int value = MINIMUM;

    public Capacity(final int value) {
        validateCapacityIsOverStandard(value);
        this.value = value;
    }

    public boolean isLeft(final int number) {
        return number < value;
    }

    private void validateCapacityIsOverStandard(final int value) {
        if (value < MINIMUM) {
            throw new IllegalArgumentException(String.format("인원은 %d 명이어야 합니다. [%d]", MINIMUM, value));
        }
    }
}
