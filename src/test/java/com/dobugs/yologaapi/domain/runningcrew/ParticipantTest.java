package com.dobugs.yologaapi.domain.runningcrew;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Participant 도메인 테스트")
class ParticipantTest {

    private static final Long HOST_ID = 0L;

    private RunningCrew runningCrew;

    @BeforeEach
    void setUp() {
        runningCrew = createRunningCrew(HOST_ID);
    }

    @DisplayName("Participant 객체 생성 테스트")
    @Nested
    public class create {

        @DisplayName("호스트 객체를 생성한다")
        @Test
        void memberIsHost() {
            final Participant host = Participant.host(runningCrew);

            assertAll(
                () -> assertThat(host.getMemberId()).isEqualTo(HOST_ID),
                () -> assertThat(host.getStatus()).isEqualTo(ParticipantType.PARTICIPATING),
                () -> assertThat(host.getRunningCrew()).isEqualTo(runningCrew)
            );
        }

        @DisplayName("참여자 객체를 생성한다")
        @Test
        void memberIsParticipant() {
            final long memberId = 1L;
            final Participant member = Participant.member(runningCrew, memberId);

            assertAll(
                () -> assertThat(member.getMemberId()).isEqualTo(memberId),
                () -> assertThat(member.getStatus()).isEqualTo(ParticipantType.REQUESTED),
                () -> assertThat(member.getRunningCrew()).isEqualTo(runningCrew)
            );
        }
    }

    @DisplayName("참여 요청 취소 테스트")
    @Nested
    public class cancel {

        @DisplayName("참여 요청했던 것을 취소한다")
        @Test
        void success() {
            final long memberId = 1L;
            final Participant member = Participant.member(runningCrew, memberId);

            member.cancel();

            assertThat(member.getStatus()).isEqualTo(ParticipantType.CANCELLED);
        }

        @DisplayName("참여 요청중이 아니면 예외가 발생한다")
        @Test
        void memberIsNotRequested() {
            final Participant participant = Participant.host(runningCrew);

            assertThatThrownBy(participant::cancel)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여 요청인 상태가 아닙니다.");
        }
    }

    @DisplayName("탈퇴 테스트")
    @Nested
    public class withdraw {

        @DisplayName("탈퇴한다")
        @Test
        void success() {
            final Participant participant = Participant.host(runningCrew);

            participant.withdraw();

            assertThat(participant.getStatus()).isEqualTo(ParticipantType.WITHDRAWN);
        }

        @DisplayName("참여중이 아니면 예외가 발생한다")
        @Test
        void memberIsNotParticipating() {
            final long memberId = 1L;
            final Participant member = Participant.member(runningCrew, memberId);

            assertThatThrownBy(member::withdraw)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여중인 상태가 아닙니다.");
        }
    }

    @DisplayName("참여 요청 승인 테스트")
    @Nested
    public class accept {

        @DisplayName("참여 요청을 승인한다")
        @Test
        void success() {
            final long memberId = 1L;
            final Participant member = Participant.member(runningCrew, memberId);

            member.accept();

            assertThat(member.getStatus()).isEqualTo(ParticipantType.PARTICIPATING);
        }

        @DisplayName("참여 요청중이 아니면 예외가 발생한다")
        @Test
        void memberIsNotRequested() {
            final Participant member = Participant.host(runningCrew);

            assertThatThrownBy(member::accept)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여 요청인 상태가 아닙니다.");
        }
    }
}
