package com.dobugs.yologaapi.domain.runningcrew;

import lombok.Getter;

@Getter
public enum ParticipantType {

    REQUESTED("참여 요청", "REQUESTED"),
    CANCELLED("참여 요청 취소", "CANCELLED"),
    PARTICIPATING("참여중", "PARTICIPATING"),
    REJECTED("참여 거절", "REJECTED"),
    WITHDRAWN("탈퇴", "WITHDRAWN"),
    ;

    private final String description;
    private final String savedName;

    ParticipantType(final String description, final String savedName) {
        this.description = description;
        this.savedName = savedName;
    }

    public boolean isRequested() {
        return this == REQUESTED;
    }

    public boolean isParticipating() {
        return this == PARTICIPATING;
    }
}
