package com.dobugs.yologaapi.domain.runningcrew;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Deadline 도메인 테스트")
class DeadlineTest {

    @DisplayName("Deadline 객체 생성 테스트")
    @Nested
    public class create {

        @DisplayName("Deadline 객체를 생성한다")
        @Test
        void success() {
            final LocalDateTime deadline = LocalDateTime.now().plusDays(1);

            assertThatCode(() -> new Deadline(deadline))
                .doesNotThrowAnyException();
        }

        @DisplayName("마감 기한은 현재 시간보다 이후여야 한다")
        @Test
        void deadlineShouldAfterThanNow() {
            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime deadline = now.minusDays(1);

            assertThatThrownBy(() -> new Deadline(deadline))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("마감 기한이 현재 시간보다 이전입니다.");
        }
    }

    @DisplayName("마감 기한 테스트")
    @Nested
    public class validateDeadlineIsNotOver {

        @DisplayName("마감 기한이 지나지 않았으면 예외가 발생하지 않는다")
        @Test
        void deadlineIsNotOver() {
            final Deadline deadline = new Deadline(LocalDateTime.now().plusDays(1));

            assertThatCode(deadline::validateDeadlineIsNotOver)
                .doesNotThrowAnyException();
        }

        @DisplayName("마감 기한이 지났으면 예외가 발생한다")
        @Test
        void deadlineIsOver() throws InterruptedException {
            final int seconds = 1;
            final Deadline deadline = new Deadline(LocalDateTime.now().plusSeconds(seconds));

            TimeUnit.SECONDS.sleep(seconds);

            assertThatThrownBy(deadline::validateDeadlineIsNotOver)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("마감기한이 지났습니다.");
        }
    }
}
