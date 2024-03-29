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
        final Participant participant = Participant.requested(runningCrew, runningCrew.getMemberId());
        participant.participate();
        value.add(participant);
    }

    public void temporaryJoin(final RunningCrew runningCrew, final Long memberId) {
        validateMemberIsNotParticipant(memberId);
        value.add(Participant.requested(runningCrew, memberId));
    }

    public void cancel(final Long memberId) {
        final Participant participant = findParticipant(memberId);
        participant.cancel();
    }

    public void withdraw(final Long memberId) {
        final Participant participant = findParticipant(memberId);
        participant.withdraw();
    }

    public void accept(final Long memberId) {
        final Participant participant = findParticipant(memberId);
        participant.accept();
    }

    public void reject(final Long memberId) {
        final Participant participant = findParticipant(memberId);
        participant.reject();
    }

    public void validateCapacityIsOver(final Capacity capacity) {
        final int numberOfParticipants = value.size();
        if (!capacity.isLeft(numberOfParticipants)) {
            throw new IllegalArgumentException("참여자 수용인원이 다 찼습니다.");
        }
    }

    private void validateMemberIsNotParticipant(final Long memberId) {
        for (final Participant participant : value) {
            if (participant.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException(String.format("이미 참여중입니다. [%s, %s]", memberId, participant.getStatus().getDescription()));
            }
        }
    }

    public int getNumberOfParticipants() {
        final List<Participant> participants = value.stream()
            .filter(value -> value.getStatus().isParticipating())
            .toList();
        return participants.size();
    }

    private Participant findParticipant(final Long memberId) {
        return value.stream()
            .filter(value -> value.getMemberId().equals(memberId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("참여자가 아닙니다. [%s]", memberId)));
    }
}
