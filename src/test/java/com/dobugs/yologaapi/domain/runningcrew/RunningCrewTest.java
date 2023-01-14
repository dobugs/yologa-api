package com.dobugs.yologaapi.domain.runningcrew;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.AFTER_ONE_HOUR;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.COORDINATES;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.NOW;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.RUNNING_CREW_CAPACITY;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.RUNNING_CREW_DESCRIPTION;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.RUNNING_CREW_TITLE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RunningCrew 도메인 테스트")
class RunningCrewTest {

    @DisplayName("러닝크루 객체 생성 테스트")
    @Nested
    public class createTest {

        @DisplayName("러닝크루를 생성한다")
        @Test
        void create() {
            assertThatCode(() -> new RunningCrew(
                    1L, COORDINATES, COORDINATES, new Capacity(RUNNING_CREW_CAPACITY),
                    NOW, AFTER_ONE_HOUR, new Deadline(AFTER_ONE_HOUR),
                    RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
            )).doesNotThrowAnyException();
        }

        @DisplayName("시작 시간은 종료 시간보다 앞서있어야 한다")
        @Test
        void startShouldBeBeforeThanEnd() {
            final LocalDateTime start = LocalDateTime.now();
            final LocalDateTime end = start.minusDays(1);

            assertThatThrownBy(
                () -> createRunningCrewWith(start, end)
            )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 시간은 종료 시간보다 앞서있어야 합니다.");
        }
    }

    @DisplayName("러닝크루 수정 테스트")
    @Nested
    public class updateTest {

        @DisplayName("러닝크루를 수정한다")
        @Test
        void update() {
            final RunningCrew runningCrew = new RunningCrew(
                1L, COORDINATES, COORDINATES, new Capacity(RUNNING_CREW_CAPACITY),
                NOW, AFTER_ONE_HOUR, new Deadline(AFTER_ONE_HOUR),
                RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
            );

            assertThatCode(() -> runningCrew.update(
                COORDINATES, COORDINATES, new Capacity(RUNNING_CREW_CAPACITY),
                NOW, AFTER_ONE_HOUR, new Deadline(AFTER_ONE_HOUR),
                RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
            )).doesNotThrowAnyException();
        }

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

    @DisplayName("러닝크루 시작 테스트")
    @Nested
    public class startTest {

        @DisplayName("러닝크루를 시작한다")
        @Test
        void start() {
            final RunningCrew runningCrew = createRunningCrew();

            runningCrew.start();

            assertThat(runningCrew.getImplementedStartDate()).isNotNull();
        }

        @DisplayName("이미 시작된 러닝크루는 다시 시작할 수 없다")
        @Test
        void runningCrewHasAlreadyBegun() {
            final RunningCrew runningCrew = createRunningCrew();

            runningCrew.start();

            assertThatThrownBy(runningCrew::start)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 시작되었습니다.");
        }
    }
}
