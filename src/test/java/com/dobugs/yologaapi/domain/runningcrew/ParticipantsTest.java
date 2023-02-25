package com.dobugs.yologaapi.domain.runningcrew;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Participants 도메인 테스트")
class ParticipantsTest {

    @DisplayName("참여자 추가 테스트")
    @Nested
    public class add {

        @DisplayName("참여자를 추가한다")
        @Test
        void success() {
            final Participants participants = new Participants(1L);

            assertThatCode(() -> participants.add(2L))
                .doesNotThrowAnyException();
        }

        @DisplayName("이미 존재하는 사용자를 추가하면 예외가 발생한다")
        @Test
        void memberIsAlreadyParticipant() {
            final long memberId = 1L;
            final Participants participants = new Participants(memberId);

            assertThatThrownBy(() -> participants.add(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 참여된 사용자입니다.");
        }
    }

    @DisplayName("인원수가 더 많은 지에 대한 테스트")
    @Nested
    public class validateCapacityIsOver {
        
        @DisplayName("참여자보다 인원 수가 더 많을 경우 예외가 발생하지 않는다")
        @Test
        void capacityIsOver() {
            final Capacity capacity = new Capacity(5);
            final Participants participants = new Participants(1L);

            assertThatCode(() -> participants.validateCapacityIsOver(capacity))
                .doesNotThrowAnyException();
        }
        
        @DisplayName("참여자보다 인원 수가 더 적을 경우 예외가 발생한다")
        @Test
        void capacityIsLower() {
            final Capacity capacity = new Capacity(2);
            final Participants participants = new Participants(1L);
            participants.add(2L);
            participants.add(3L);

            assertThatThrownBy(() -> participants.validateCapacityIsOver(capacity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여자 수용인원이 다 찼습니다.");
        }
    }
}
