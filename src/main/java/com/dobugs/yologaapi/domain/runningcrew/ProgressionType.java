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

    public boolean isCreatedOrReady() {
        return List.of(CREATED, READY).contains(this);
    }

    public boolean isCreatedOrReadyOrInProgress() {
        return List.of(CREATED, READY, IN_PROGRESS).contains(this);
    }
}
