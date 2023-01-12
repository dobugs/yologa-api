package com.dobugs.yologaapi.domain.runningcrew;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Deadline 도메인 테스트")
class DeadlineTest {

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
