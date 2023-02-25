package com.dobugs.yologaapi.domain.runningcrew;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

public class Participants {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "running_crew_id")
    private List<Participant> value = new ArrayList<>();

    public Participants(final Long memberId) {
        value.add(new Participant(memberId));
    }

    public void add(final Long memberId) {
        final List<Long> participants = value.stream().map(Participant::getMemberId).toList();
        if (participants.contains(memberId)) {
            throw new IllegalArgumentException("이미 참여된 사용자입니다.");
        }
        value.add(new Participant(memberId));
    }

    public void validateCapacityIsOver(final Capacity capacity) {
        final int numberOfParticipants = value.size();
        if (!capacity.isLeft(numberOfParticipants)) {
            throw new IllegalArgumentException("참여자 수용인원이 다 찼습니다.");
        }
    }
}
