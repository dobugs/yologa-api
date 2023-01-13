package com.dobugs.yologaapi.domain.runningcrew;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RunningCrew 도메인 테스트")
class RunningCrewTest {

    private static final double LATITUDE = 123.456;
    private static final double LONGITUDE = 123.456;

    private static final String RUNNING_CREW_TITLE = "title";
    private static final int RUNNING_CREW_CAPACITY = 10;
    private static final String RUNNING_CREW_DESCRIPTION = "description";

    private static final Coordinates COORDINATES = new Coordinates(LATITUDE, LONGITUDE);

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime AFTER_ONE_HOUR = LocalDateTime.now().plusHours(1);

    @DisplayName("러닝크루 객체 생성 테스트")
    @Nested
    public class createTest {

        @DisplayName("시작 시간은 종료 시간보다 앞서있어야 한다")
        @Test
        void startShouldBeBeforeThanEnd() {
            final LocalDateTime start = LocalDateTime.now();
            final LocalDateTime end = start.minusDays(1);

            assertThatThrownBy(
                () -> new RunningCrew(
                    1L, COORDINATES, COORDINATES,
                    new Capacity(RUNNING_CREW_CAPACITY), start, end,
                    new Deadline(AFTER_ONE_HOUR),
                    RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
                )
            )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 시간은 종료 시간보다 앞서있어야 합니다.");
        }
    }

    @DisplayName("러닝크루 수정 테스트")
    @Nested
    public class updateTest {

        @DisplayName("시작 시간은 종료 시간보다 앞서있어야 한다")
        @Test
        void startShouldBeBeforeThanEnd() {
            final LocalDateTime start = LocalDateTime.now();
            final LocalDateTime end = start.minusDays(1);

            final RunningCrew runningCrew = createRunningCrew();

            assertThatThrownBy(
                () -> runningCrew.update(
                    COORDINATES, COORDINATES,
                    new Capacity(RUNNING_CREW_CAPACITY), start, end,
                    new Deadline(AFTER_ONE_HOUR),
                    RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
                )
            )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 시간은 종료 시간보다 앞서있어야 합니다.");
        }
    }

    private RunningCrew createRunningCrew() {
        return new RunningCrew(
            1L, COORDINATES, COORDINATES,
            new Capacity(RUNNING_CREW_CAPACITY), NOW, AFTER_ONE_HOUR,
            new Deadline(AFTER_ONE_HOUR),
            RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
        );
    }
}
