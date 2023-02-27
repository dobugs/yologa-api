package com.dobugs.yologaapi.domain.runningcrew;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ParticipantType 도메인 테스트")
class ParticipantTypeTest {

    @DisplayName("요청 중인지에 대한 테스트")
    @Nested
    public class isRequested {

        @DisplayName("요청 중일 경우 true 를 반환한다")
        @Test
        void participantTypeIsRequested() {
            assertThat(ParticipantType.REQUESTED.isRequested()).isTrue();
        }

        @DisplayName("요청 중이 아닐 경우 false 를 반환한다")
        @Test
        void participantTypeIsNotRequested() {
            assertAll(
                () -> assertThat(ParticipantType.PARTICIPATING.isRequested()).isFalse(),
                () -> assertThat(ParticipantType.REJECTED.isRequested()).isFalse(),
                () -> assertThat(ParticipantType.WITHDRAWN.isRequested()).isFalse()
            );
        }
    }

    @DisplayName("참여중인지에 대한 테스트")
    @Nested
    public class isParticipating {

        @DisplayName("참여중일 경우 true 를 반환한다")
        @Test
        void participantTypeIsParticipating() {
            assertThat(ParticipantType.PARTICIPATING.isParticipating()).isTrue();
        }

        @DisplayName("참여중이 아닐 경우 false 를 반환한다")
        @Test
        void participantTypeIsNotParticipating() {
            assertAll(
                () -> assertThat(ParticipantType.REQUESTED.isParticipating()).isFalse(),
                () -> assertThat(ParticipantType.REJECTED.isParticipating()).isFalse(),
                () -> assertThat(ParticipantType.WITHDRAWN.isParticipating()).isFalse()
            );
        }
    }
}
