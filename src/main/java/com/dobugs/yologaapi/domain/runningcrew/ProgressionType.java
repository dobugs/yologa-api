package com.dobugs.yologaapi.domain.runningcrew;

import java.util.List;

import lombok.Getter;

@Getter
public enum ProgressionType {

    CREATED("생성", "CREATED"),
    READY("준비", "READY"),
    IN_PROGRESS("진행중", "IN_PROGRESS"),
    COMPLETED("완료", "COMPLETED"),
    CANCELLED("취소", "CANCELLED"),
    EXPIRED("만료", "EXPIRED"),
    ;

    private final String description;
    private final String savedName;

    ProgressionType(final String description, final String savedName) {
        this.description = description;
        this.savedName = savedName;
    }

    public static ProgressionType from(final String value) {
        try {
            return ProgressionType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("잘못된 상태값입니다. [%s]", value));
        }
    }

    public boolean isCreated() {
        return this.equals(CREATED);
    }

    public boolean isCreatedOrReady() {
        return List.of(CREATED, READY).contains(this);
    }

    public boolean isCreatedOrReadyOrInProgress() {
        return List.of(CREATED, READY, IN_PROGRESS).contains(this);
    }
}
