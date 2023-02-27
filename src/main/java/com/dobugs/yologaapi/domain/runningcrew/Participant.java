package com.dobugs.yologaapi.domain.runningcrew;

import com.dobugs.yologaapi.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long memberId;

    @Enumerated(value = EnumType.STRING)
    private ParticipantType status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_crew_id")
    private RunningCrew runningCrew;

    public Participant(final Long memberId, final ParticipantType status, final RunningCrew runningCrew) {
        this.memberId = memberId;
        this.status = status;
        this.runningCrew = runningCrew;
    }

    public static Participant host(final RunningCrew runningCrew) {
        return new Participant(runningCrew.getMemberId(), ParticipantType.PARTICIPATING, runningCrew);
    }

    public static Participant member(final RunningCrew runningCrew, final Long memberId) {
        return new Participant(memberId, ParticipantType.REQUESTED, runningCrew);
    }

    public void cancel() {
        validateMemberIsRequested(memberId);
        this.status = ParticipantType.CANCELLED;
    }

    public void withdraw() {
        validateMemberIsParticipating(memberId);
        this.status = ParticipantType.WITHDRAWN;
    }

    public boolean isRequested() {
        return status.isRequested();
    }

    private void validateMemberIsRequested(final Long memberId) {
        if (!status.isRequested()) {
            throw new IllegalArgumentException(String.format("참여 요청인 상태가 아닙니다. [%s, %s]", memberId, status.getName()));
        }
    }

    private void validateMemberIsParticipating(final Long memberId) {
        if (!status.isParticipating()) {
            throw new IllegalArgumentException(String.format("참여중인 상태가 아닙니다. [%s, %s]", memberId, status.getName()));
        }
    }
}
