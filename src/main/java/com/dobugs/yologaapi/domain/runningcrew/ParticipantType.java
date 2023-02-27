package com.dobugs.yologaapi.domain.runningcrew;

import lombok.Getter;

@Getter
public enum ParticipantType {

    REQUESTED("참여 요청"),
    CANCELLED("참여 요청 취소"),
    PARTICIPATING("참여중"),
    REJECTED("참여 거절"),
    WITHDRAWN("탈퇴"),
    ;

    private final String name;

    ParticipantType(final String name) {
        this.name = name;
    }

    public boolean isRequested() {
        return this == REQUESTED;
    }

    public boolean isParticipating() {
        return this == PARTICIPATING;
    }
}
