package com.dobugs.yologaapi.domain.runningcrew;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Capacity 도메인 테스트")
class CapacityTest {

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
