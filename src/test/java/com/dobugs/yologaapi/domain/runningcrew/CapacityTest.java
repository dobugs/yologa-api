package com.dobugs.yologaapi.domain.runningcrew;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Capacity 도메인 테스트")
class CapacityTest {

    @DisplayName("수용인원이 남았는지에 대한 테스트")
    @Nested
    public class isLeft {

        private static final int VALUE = 5;

        @DisplayName("수용인원이 남았을 경우 true 를 반환한다")
        @Test
        void capacityIsLeft() {
            final int numberOfParticipants = 1;
            final Capacity capacity = new Capacity(VALUE);

            assertThat(capacity.isLeft(numberOfParticipants)).isTrue();
        }

        @DisplayName("수용인원이 남지 않았을 경우 false 를 반환한다")
        @ParameterizedTest
        @ValueSource(ints = {VALUE, VALUE + 1})
        void capacityIsFull(final int numberOfParticipants) {
            final Capacity capacity = new Capacity(VALUE);

            assertThat(capacity.isLeft(numberOfParticipants)).isFalse();
        }
    }

    @DisplayName("Capacity 의 최저 인원 테스트")
    @Nested
    public class validateCapacityIsOverStandard {

        @DisplayName("Capacity 의 값은 기준치 보다 커야 한다")
        @ParameterizedTest
        @ValueSource(ints = {-1, 0, 1})
        void capacityShouldOverStandard(int value) {
            assertThatThrownBy(() -> new Capacity(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("인원은")
                .hasMessageContaining("명이어야 합니다.");
        }
    }
}
