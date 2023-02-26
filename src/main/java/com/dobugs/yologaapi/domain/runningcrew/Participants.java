package com.dobugs.yologaapi.domain.runningcrew;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Participants {

    @OneToMany(mappedBy = "runningCrew", cascade = CascadeType.ALL)
    private List<Participant> value = new ArrayList<>();

    public Participants(final RunningCrew runningCrew) {
        value.add(new Participant(runningCrew));
    }

    public void add(final RunningCrew runningCrew, final Long memberId) {
        validateMemberIsNotParticipant(memberId);
        value.add(new Participant(memberId, runningCrew));
    }

    public void delete(final Long memberId) {
        final Participant participant = findParticipant(memberId);
        value.remove(participant);
    }

    public void validateCapacityIsOver(final Capacity capacity) {
        final int numberOfParticipants = value.size();
        if (!capacity.isLeft(numberOfParticipants)) {
            throw new IllegalArgumentException("참여자 수용인원이 다 찼습니다.");
        }
    }

    public void validateMemberIsNotParticipant(final Long memberId) {
        for (final Participant participant : value) {
            if (participant.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException(String.format("이미 참여중입니다. [%s, %s]", memberId, participant.getStatus().getName()));
            }
        }
    }

    public void validateMemberIsRequested(final Long memberId) {
        final Participant participant = findParticipant(memberId);
        if (!participant.isRequested()) {
            throw new IllegalArgumentException(String.format("참여 요청인 상태가 아닙니다. [%s, %s]", memberId, participant.getStatus().getName()));
        }
    }

    private Participant findParticipant(final Long memberId) {
        return value.stream()
            .filter(value -> value.getMemberId().equals(memberId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("참여자가 아닙니다. [%s]", memberId)));
    }
}
