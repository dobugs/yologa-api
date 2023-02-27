package com.dobugs.yologaapi.domain.runningcrew;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Participants 도메인 테스트")
class ParticipantsTest {

    private static final long HOST_ID = 1L;

    private RunningCrew runningCrew;

    @BeforeEach
    void setUp() {
        runningCrew = createRunningCrew(HOST_ID);
    }

    @DisplayName("참여자 추가 테스트")
    @Nested
    public class add {

        @DisplayName("참여자를 추가한다")
        @Test
        void success() {
            final long memberId = 2L;
            final Participants participants = new Participants(runningCrew);

            participants.add(runningCrew, memberId);

            final List<Long> idsOfParticipants = participants.getValue()
                .stream()
                .map(Participant::getMemberId)
                .toList();
            assertThat(idsOfParticipants).contains(memberId);
        }

        @DisplayName("이미 존재하는 사용자를 추가하면 예외가 발생한다")
        @Test
        void memberIsAlreadyParticipant() {
            final Participants participants = new Participants(runningCrew);

            assertThatThrownBy(() -> participants.add(runningCrew, HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 참여중입니다.");
        }
    }

    @DisplayName("참여자 삭제 테스트")
    @Nested
    public class delete {

        @DisplayName("참여자를 삭제한다")
        @Test
        void success() {
            final long memberId = 2L;
            final Participants participants = new Participants(runningCrew);
            participants.add(runningCrew, memberId);

            participants.delete(memberId);

            final List<Long> idsOfParticipants = participants.getValue()
                .stream()
                .map(Participant::getMemberId)
                .toList();
            assertThat(idsOfParticipants).doesNotContain(memberId);
        }

        @DisplayName("참여자가 아닐 경우 예외가 발생한다")
        @Test
        void memberIsNotParticipant() {
            final long memberId = 2L;
            final Participants participants = new Participants(runningCrew);

            assertThatThrownBy(() -> participants.delete(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여자가 아닙니다.");
        }
    }

    @DisplayName("탈퇴 테스트")
    @Nested
    public class withdraw {

        @DisplayName("탈퇴한다")
        @Test
        void success() {
            final Participants participants = new Participants(runningCrew);

            participants.withdraw(HOST_ID);

            final Participant participant = participants.getValue().stream()
                .filter(value -> value.getMemberId().equals(HOST_ID))
                .findFirst().get();
            assertThat(participant.getStatus()).isEqualTo(ParticipantType.WITHDRAWN);
        }

        @DisplayName("참여자가 아닐 경우 예외가 발생한다")
        @Test
        void memberIsNotParticipant() {
            final long memberId = 2L;
            final Participants participants = new Participants(runningCrew);

            assertThatThrownBy(() -> participants.withdraw(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여자가 아닙니다.");
        }
    }

    @DisplayName("인원수가 더 많은 지에 대한 테스트")
    @Nested
    public class validateCapacityIsOver {

        @DisplayName("참여자보다 인원 수가 더 많을 경우 예외가 발생하지 않는다")
        @Test
        void capacityIsOver() {
            final Capacity capacity = new Capacity(5);
            final Participants participants = new Participants(runningCrew);

            assertThatCode(() -> participants.validateCapacityIsOver(capacity))
                .doesNotThrowAnyException();
        }

        @DisplayName("참여자보다 인원 수가 더 적을 경우 예외가 발생한다")
        @Test
        void capacityIsLower() {
            final Capacity capacity = new Capacity(2);
            final Participants participants = new Participants(runningCrew);
            participants.add(runningCrew, 2L);
            participants.add(runningCrew, 3L);

            assertThatThrownBy(() -> participants.validateCapacityIsOver(capacity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여자 수용인원이 다 찼습니다.");
        }
    }

    @DisplayName("참여자가 아닌 것에 대한 테스트")
    @Nested
    public class validateMemberIsNotParticipant {

        @DisplayName("참여자가 아닐 경우 예외가 발생하지 않는다")
        @Test
        void memberIsNotParticipant() {
            final long memberId = 2L;
            final Participants participants = new Participants(runningCrew);

            assertThatCode(() -> participants.validateMemberIsNotParticipant(memberId))
                .doesNotThrowAnyException();
        }

        @DisplayName("참여자일 경우 예외가 발생한다")
        @Test
        void memberIsParticipant() {
            final Participants participants = new Participants(runningCrew);

            assertThatThrownBy(() -> participants.validateMemberIsNotParticipant(HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 참여중입니다.");
        }
    }

    @DisplayName("사용자가 참여 요청을 했었지에 대한 검증 테스트")
    @Nested
    public class validateMemberIsRequested {

        @DisplayName("이전에 참여 요청을 했었으면 예외가 발생하지 않는다")
        @Test
        void memberIsRequested() {
            final long memberId = 2L;
            final Participants participants = new Participants(runningCrew);
            participants.add(runningCrew, memberId);

            assertThatCode(() -> participants.validateMemberIsRequested(memberId))
                .doesNotThrowAnyException();
        }

        @DisplayName("참여 요청이 아닌 다른 상태값이면 예외가 발생한다")
        @Test
        void memberIsNotRequested() {
            final Participants participants = new Participants(runningCrew);

            assertThatThrownBy(() -> participants.validateMemberIsRequested(HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여 요청인 상태가 아닙니다.");
        }

        @DisplayName("참여자가 아닐 경우 예외가 발생한다")
        @Test
        void memberIsNotParticipant() {
            final long memberId = 2L;
            final Participants participants = new Participants(runningCrew);

            assertThatThrownBy(() -> participants.validateMemberIsRequested(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여자가 아닙니다.");
        }
    }

    @DisplayName("참여중인 사용자 수 조회 테스트")
    @Nested
    public class getNumberOrParticipants {

        @DisplayName("참여자 목록 중 '참여중' 인 사용자의 수를 조회한다")
        @Test
        void success() {
            final Participants participants = new Participants(runningCrew);
            participants.add(runningCrew, 2L);
            participants.add(runningCrew, 3L);

            final int numberOrParticipants = participants.getNumberOrParticipants();

            assertThat(numberOrParticipants).isEqualTo(1);
        }
    }
}
