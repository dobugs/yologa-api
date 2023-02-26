package com.dobugs.yologaapi.domain.runningcrew;

import java.util.List;

import lombok.Getter;

@Getter
public enum ProgressionType {

    CREATED("생성"),
    READY("준비"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    CANCELLED("취소"),
    EXPIRED("만료"),
    ;

    private final String name;

    ProgressionType(final String name) {
        this.name = name;
    }

    public boolean isReady() {
        final List<ProgressionType> readied = List.of(CREATED, READY);
        return readied.contains(this);
    }
}
