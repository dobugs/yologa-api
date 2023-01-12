package com.dobugs.yologaapi.domain.runningcrew;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Capacity {

    @Column(name = "capacity", nullable = false)
    private int value = 0;

    protected Capacity() {
    }

    public Capacity(final int value) {
        this.value = value;
    }
}
