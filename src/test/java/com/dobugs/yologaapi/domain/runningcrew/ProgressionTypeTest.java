package com.dobugs.yologaapi.domain.runningcrew;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ProgressionType 도메인 테스트")
class ProgressionTypeTest {

    @DisplayName("시작 테스트")
    @Nested
    public class isReady {

        @DisplayName("준비중이면 true 를 반환한다")
        @Test
        void ready() {
            assertAll(
                () -> assertThat(ProgressionType.CREATED.isReady()).isTrue(),
                () -> assertThat(ProgressionType.READY.isReady()).isTrue()
            );
        }

        @DisplayName("준비가 끝났으면 false 를 반환한다")
        @Test
        void notReady() {
            assertAll(
                () -> assertThat(ProgressionType.IN_PROGRESS.isReady()).isFalse(),
                () -> assertThat(ProgressionType.COMPLETED.isReady()).isFalse(),
                () -> assertThat(ProgressionType.CANCELLED.isReady()).isFalse(),
                () -> assertThat(ProgressionType.EXPIRED.isReady()).isFalse()
            );
        }
    }
}
