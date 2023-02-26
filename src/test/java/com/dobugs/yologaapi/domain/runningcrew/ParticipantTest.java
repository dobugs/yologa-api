package com.dobugs.yologaapi.domain.runningcrew;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static org.assertj.core.api.Assertions.assertThat;
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
            final Participant participant = new Participant(runningCrew);

            assertAll(
                () -> assertThat(participant.getMemberId()).isEqualTo(HOST_ID),
                () -> assertThat(participant.getStatus()).isEqualTo(ParticipantType.PARTICIPATING),
                () -> assertThat(participant.getRunningCrew()).isEqualTo(runningCrew)
            );
        }

        @DisplayName("참여자 객체를 생성한다")
        @Test
        void memberIsParticipant() {
            final long memberId = 1L;
            final Participant participant = new Participant(memberId, runningCrew);

            assertAll(
                () -> assertThat(participant.getMemberId()).isEqualTo(memberId),
                () -> assertThat(participant.getStatus()).isEqualTo(ParticipantType.REQUESTED),
                () -> assertThat(participant.getRunningCrew()).isEqualTo(runningCrew)
            );
        }
    }

    @DisplayName("요청 중인지에 대한 테스트")
    @Nested
    public class isRequested {

        @DisplayName("요청 중일 경우 true 를 반환한다")
        @Test
        void participantTypeIsRequested() {
            final long memberId = 1L;
            final Participant participant = new Participant(memberId, runningCrew);

            final boolean requested = participant.isRequested();

            assertThat(requested).isTrue();
        }

        @DisplayName("요청 중이 아닐 경우 false 를 반환한다")
        @Test
        void participantTypeIsNotRequested() {
            final Participant participant = new Participant(runningCrew);

            final boolean requested = participant.isRequested();

            assertThat(requested).isFalse();
        }
    }
}
