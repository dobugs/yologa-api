package com.dobugs.yologaapi.domain.runningcrew;

import com.dobugs.yologaapi.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public Participant(final Long memberId) {
        this.memberId = memberId;
        this.status = ParticipantType.PARTICIPATING;
    }
}
