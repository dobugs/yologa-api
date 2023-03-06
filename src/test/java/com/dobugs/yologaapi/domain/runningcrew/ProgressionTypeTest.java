package com.dobugs.yologaapi.domain.runningcrew;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ProgressionType 도메인 테스트")
class ProgressionTypeTest {

    @DisplayName("Enum 이름으로 ProgressionType 객체 조회 테스트")
    @Nested
    public class from {

        @DisplayName("Enum 이 존재할 경우 올바른 객체를 반환한다")
        @Test
        void success() {
            final String name = "CREATED";

            final ProgressionType progressionType = ProgressionType.from(name);

            assertThat(progressionType).isEqualTo(ProgressionType.CREATED);
        }

        @DisplayName("Enum 이 존재하지 않을 경우 예외가 발생한다")
        @Test
        void notExist() {
            final String invalidProgressionType = "invalidProgressionType";

            assertThatThrownBy(() -> ProgressionType.from(invalidProgressionType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 상태값입니다.");
        }
    }

    @DisplayName("생성 또는 준비에 대한 테스트")
    @Nested
    public class isCreatedOrReady {

        @DisplayName("생성 또는 준비중이면 true 를 반환한다")
        @Test
        void createdOrReady() {
            assertAll(
                () -> assertThat(ProgressionType.CREATED.isCreatedOrReady()).isTrue(),
                () -> assertThat(ProgressionType.READY.isCreatedOrReady()).isTrue()
            );
        }

        @DisplayName("생성 또는 준비중이 아니면 false 를 반환한다")
        @Test
        void notCreatedAndReady() {
            assertAll(
                () -> assertThat(ProgressionType.IN_PROGRESS.isCreatedOrReady()).isFalse(),
                () -> assertThat(ProgressionType.COMPLETED.isCreatedOrReady()).isFalse(),
                () -> assertThat(ProgressionType.CANCELLED.isCreatedOrReady()).isFalse(),
                () -> assertThat(ProgressionType.EXPIRED.isCreatedOrReady()).isFalse()
            );
        }
    }

    @DisplayName("생성 또는 준비 또는 진행중에 대한 테스트")
    @Nested
    public class isCreatedOrReadyOrInProgress {

        @DisplayName("생성 또는 준비 또는 진행중이면 true 를 반환한다")
        @Test
        void createdOrReadyOrInProgress() {
            assertAll(
                () -> assertThat(ProgressionType.CREATED.isCreatedOrReadyOrInProgress()).isTrue(),
                () -> assertThat(ProgressionType.READY.isCreatedOrReadyOrInProgress()).isTrue(),
                () -> assertThat(ProgressionType.IN_PROGRESS.isCreatedOrReadyOrInProgress()).isTrue()
            );
        }

        @DisplayName("생성 또는 준비 또는 진행중이 아니면 false 를 반환한다")
        @Test
        void notCreatedAndReadyAndInProgress() {
            assertAll(
                () -> assertThat(ProgressionType.COMPLETED.isCreatedOrReadyOrInProgress()).isFalse(),
                () -> assertThat(ProgressionType.CANCELLED.isCreatedOrReadyOrInProgress()).isFalse(),
                () -> assertThat(ProgressionType.EXPIRED.isCreatedOrReadyOrInProgress()).isFalse()
            );
        }
    }
}
