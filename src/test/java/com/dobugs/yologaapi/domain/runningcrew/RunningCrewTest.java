package com.dobugs.yologaapi.domain.runningcrew;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.AFTER_ONE_HOUR;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.COORDINATES;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.NOW;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.RUNNING_CREW_CAPACITY;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.RUNNING_CREW_DESCRIPTION;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.RUNNING_CREW_TITLE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewWithDeadline;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewWithScheduledDate;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewWithCapacity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RunningCrew 도메인 테스트")
class RunningCrewTest {

    @DisplayName("러닝크루 객체 생성 테스트")
    @Nested
    public class create {

        private static final Long HOST_ID = 0L;

        @DisplayName("러닝크루를 생성한다")
        @Test
        void success() {
            final RunningCrew runningCrew = new RunningCrew(
                HOST_ID, COORDINATES, COORDINATES, new Capacity(RUNNING_CREW_CAPACITY),
                NOW, AFTER_ONE_HOUR, new Deadline(AFTER_ONE_HOUR),
                RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
            );
            assertAll(
                () -> assertThat(runningCrew.getStatus()).isEqualTo(ProgressionType.CREATED),
                () -> assertThat(runningCrew.getParticipants().getValue()).hasSize(1),
                () -> assertThat(runningCrew.getNumberOfParticipants()).isEqualTo(1)
            );
        }

        @DisplayName("시작 시간은 종료 시간보다 앞서있어야 한다")
        @Test
        void startShouldBeBeforeThanEnd() {
            final LocalDateTime start = LocalDateTime.now();
            final LocalDateTime end = start.minusDays(1);

            assertThatThrownBy(
                () -> createRunningCrewWithScheduledDate(start, end)
            )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 시간은 종료 시간보다 앞서있어야 합니다.");
        }
    }

    @DisplayName("러닝크루 수정 테스트")
    @Nested
    public class update {

        private static final Long HOST_ID = 0L;

        @DisplayName("러닝크루를 수정한다")
        @Test
        void success() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatCode(() -> runningCrew.update(
                HOST_ID,
                COORDINATES, COORDINATES, new Capacity(RUNNING_CREW_CAPACITY),
                NOW, AFTER_ONE_HOUR, new Deadline(AFTER_ONE_HOUR),
                RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
            )).doesNotThrowAnyException();
        }

        @DisplayName("호스트가 아닌 경우 예외가 발생한다")
        @Test
        void memberIsNotHost() {
            final long memberId = -1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.update(
                memberId,
                COORDINATES, COORDINATES, new Capacity(RUNNING_CREW_CAPACITY),
                NOW, AFTER_ONE_HOUR, new Deadline(AFTER_ONE_HOUR),
                RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
            )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("호스트가 아닙니다.");
        }

        @DisplayName("시작 시간은 종료 시간보다 앞서있어야 한다")
        @Test
        void startShouldBeBeforeThanEnd() {
            final LocalDateTime start = LocalDateTime.now();
            final LocalDateTime end = start.minusDays(1);
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.update(
                HOST_ID,
                COORDINATES, COORDINATES, new Capacity(RUNNING_CREW_CAPACITY),
                start, end, new Deadline(AFTER_ONE_HOUR),
                RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
            )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 시간은 종료 시간보다 앞서있어야 합니다.");
        }

        @DisplayName("수용 인원은 현재 참여중인 인원보다 커야 한다")
        @Test
        void capacityIsLowerThanNumberOfParticipants() {
            final Capacity capacity = new Capacity(2);
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            runningCrew.participate(1L);
            runningCrew.accept(HOST_ID, 1L);
            runningCrew.participate(2L);
            runningCrew.accept(HOST_ID, 2L);

            assertThatThrownBy(() -> runningCrew.update(
                HOST_ID,
                COORDINATES, COORDINATES, capacity,
                NOW, AFTER_ONE_HOUR, new Deadline(AFTER_ONE_HOUR),
                RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
            )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여자 수용인원이 다 찼습니다.");
        }
    }

    @DisplayName("러닝크루 삭제 테스트")
    @Nested
    public class delete {

        private static final Long HOST_ID = 0L;

        @DisplayName("러닝크루를 삭제한다")
        @Test
        void success() {
            final RunningCrew runningCrew = new RunningCrew(
                HOST_ID, COORDINATES, COORDINATES, new Capacity(RUNNING_CREW_CAPACITY),
                NOW, AFTER_ONE_HOUR, new Deadline(AFTER_ONE_HOUR),
                RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
            );

            runningCrew.delete(HOST_ID);

            assertThat(runningCrew.isArchived()).isFalse();
        }

        @DisplayName("호스트가 아닐 경우 예외가 발생한다")
        @Test
        void memberIsNotHost() {
            final long memberId = -1L;

            final RunningCrew runningCrew = new RunningCrew(
                HOST_ID, COORDINATES, COORDINATES, new Capacity(RUNNING_CREW_CAPACITY),
                NOW, AFTER_ONE_HOUR, new Deadline(AFTER_ONE_HOUR),
                RUNNING_CREW_TITLE, RUNNING_CREW_DESCRIPTION
            );

            assertThatThrownBy(() -> runningCrew.delete(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("호스트가 아닙니다.");
        }
    }

    @DisplayName("러닝크루 시작 테스트")
    @Nested
    public class start {

        private static final Long HOST_ID = 0L;

        @DisplayName("러닝크루를 시작한다")
        @Test
        void success() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            runningCrew.start(HOST_ID);

            assertAll(
                () -> assertThat(runningCrew.getImplementedStartDate()).isNotNull(),
                () -> assertThat(runningCrew.getStatus()).isEqualTo(ProgressionType.IN_PROGRESS)
            );
        }

        @DisplayName("호스트가 아닐 경우 예외가 발생한다")
        @Test
        void memberIsNotHost() {
            final long memberId = -1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.start(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("호스트가 아닙니다.");
        }

        @DisplayName("이미 시작된 러닝크루는 다시 시작할 수 없다")
        @Test
        void runningCrewHasAlreadyBegun() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            runningCrew.start(HOST_ID);

            assertThatThrownBy(() -> runningCrew.start(HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 시작되었습니다.");
        }
    }

    @DisplayName("러닝크루 종료 테스트")
    @Nested
    public class end {

        private static final Long HOST_ID = 0L;

        @DisplayName("러닝크루를 종료한다")
        @Test
        void success() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            runningCrew.start(HOST_ID);
            runningCrew.end(HOST_ID);

            assertAll(
                () -> assertThat(runningCrew.getImplementedEndDate()).isNotNull(),
                () -> assertThat(runningCrew.getStatus()).isEqualTo(ProgressionType.COMPLETED)
            );
        }

        @DisplayName("호스트가 아닐 경우 예외가 발생한다")
        @Test
        void memberIsNotHost() {
            final long memberId = -1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.end(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("호스트가 아닙니다.");
        }

        @DisplayName("시작되지 않은 러닝크루는 종료할 수 없다")
        @Test
        void runningCrewHasNotStarted() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.end(HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("아직 시작하지 않았습니다.");
        }

        @DisplayName("이미 종료된 러닝크루는 다시 종료할 수 없다")
        @Test
        void runningCrewHasAlreadyEnded() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            runningCrew.start(HOST_ID);
            runningCrew.end(HOST_ID);

            assertThatThrownBy(() -> runningCrew.end(HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 종료되었습니다.");
        }
    }

    @DisplayName("참여 요청 테스트")
    @Nested
    public class participate {

        private static final Long HOST_ID = 0L;

        @DisplayName("러닝크루에 참여 요청을 한다")
        @Test
        void success() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            runningCrew.participate(memberId);

            final Optional<Participant> participant = runningCrew.getParticipants()
                .getValue()
                .stream()
                .filter(value -> value.getMemberId().equals(memberId))
                .findFirst();
            assertThat(participant).isPresent();
            assertThat(participant.get().getStatus()).isEqualTo(ParticipantType.REQUESTED);
        }

        @DisplayName("호스트일 경우 예외가 발생한다")
        @Test
        void memberIsHost() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.participate(HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루의 호스트입니다.");
        }

        @DisplayName("수용 인원이 다 찼을 경우 예외가 발생한다")
        @Test
        void capacityIsFull() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrewWithCapacity(HOST_ID, 2);
            runningCrew.participate(99L);

            assertThatThrownBy(() -> runningCrew.participate(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여자 수용인원이 다 찼습니다.");
        }

        @DisplayName("마감 기한이 지났을 경우 예외가 발생한다")
        @Test
        void deadlineIsOver() throws InterruptedException {
            final long memberId = 1L;
            final int seconds = 1;
            final RunningCrew runningCrew = createRunningCrewWithDeadline(HOST_ID, LocalDateTime.now().plusSeconds(seconds));

            TimeUnit.SECONDS.sleep(seconds);

            assertThatThrownBy(() -> runningCrew.participate(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("마감기한이 지났습니다.");
        }

        @DisplayName("러닝크루의 상태가 생성 또는 준비 상태가 아니면 예외가 발생한다")
        @Test
        void statusIsNotCreatedAndReady() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.start(HOST_ID);

            assertThatThrownBy(() -> runningCrew.participate(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 진행중이거나 완료되었습니다.");
        }
    }

    @DisplayName("참여 요청 취소 테스트")
    @Nested
    public class cancel {

        private static final Long HOST_ID = 0L;

        @DisplayName("러닝크루에 참여 요청했던 것을 취소한다")
        @Test
        void success() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            runningCrew.participate(memberId);
            runningCrew.cancel(memberId);

            final Optional<Participant> participant = runningCrew.getParticipants()
                .getValue()
                .stream()
                .filter(value -> value.getMemberId().equals(memberId))
                .findFirst();
            assertThat(participant).isPresent();
            assertThat(participant.get().getStatus()).isEqualTo(ParticipantType.CANCELLED);
        }

        @DisplayName("호스트일 경우 예외가 발생한다")
        @Test
        void memberIsHost() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.cancel(HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루의 호스트입니다.");
        }

        @DisplayName("참여 요청하지 않았었으면 예외가 발생한다")
        @Test
        void memberIsNotRequested() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.cancel(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참여자가 아닙니다.");
        }
    }

    @DisplayName("탈퇴 테스트")
    @Nested
    public class withdraw {

        private static final Long HOST_ID = 0L;

        @DisplayName("탈퇴한다")
        @Test
        void success() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.participate(memberId);
            runningCrew.accept(HOST_ID, memberId);

            runningCrew.withdraw(memberId);

            final Optional<Participant> participant = runningCrew.getParticipants()
                .getValue()
                .stream()
                .filter(value -> value.getMemberId().equals(memberId))
                .findFirst();
            assertThat(participant).isPresent();
            assertAll(
                () -> assertThat(participant.get().getStatus()).isEqualTo(ParticipantType.WITHDRAWN),
                () -> assertThat(runningCrew.getStatus()).isEqualTo(ProgressionType.CREATED),
                () -> assertThat(runningCrew.getNumberOfParticipants()).isEqualTo(1)
            );
        }

        @DisplayName("호스트일 경우 예외가 발생한다")
        @Test
        void memberIsHost() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.withdraw(HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루의 호스트입니다.");
        }
    }

    @DisplayName("참여 요청 승인 테스트")
    @Nested
    public class accept {

        private static final Long HOST_ID = 0L;

        @DisplayName("참여 요청을 승인한다")
        @Test
        void success() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.participate(memberId);

            runningCrew.accept(HOST_ID, memberId);

            final Optional<Participant> participant = runningCrew.getParticipants()
                .getValue()
                .stream()
                .filter(value -> value.getMemberId().equals(memberId))
                .findFirst();
            assertThat(participant).isPresent();
            assertAll(
                () -> assertThat(participant.get().getStatus()).isEqualTo(ParticipantType.PARTICIPATING),
                () -> assertThat(runningCrew.getStatus()).isEqualTo(ProgressionType.READY),
                () -> assertThat(runningCrew.getNumberOfParticipants()).isEqualTo(2)
            );
        }

        @DisplayName("승인자가 호스트가 아닐 경우 예외가 발생한다")
        @Test
        void memberIsNotHost() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.accept(memberId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("호스트가 아닙니다.");
        }

        @DisplayName("호스트일 경우 예외가 발생한다")
        @Test
        void memberIsHost() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.accept(HOST_ID, HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루의 호스트입니다.");
        }

        @DisplayName("마감 기한이 지났을 경우 예외가 발생한다")
        @Test
        void deadlineIsOver() throws InterruptedException {
            final long memberId = 1L;
            final int seconds = 1;
            final RunningCrew runningCrew = createRunningCrewWithDeadline(HOST_ID, LocalDateTime.now().plusSeconds(seconds));
            runningCrew.participate(memberId);

            TimeUnit.SECONDS.sleep(seconds);

            assertThatThrownBy(() -> runningCrew.accept(HOST_ID, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("마감기한이 지났습니다.");
        }

        @DisplayName("러닝크루의 상태가 생성 또는 준비 또는 진행중 상태가 아니면 예외가 발생한다")
        @Test
        void statusIsNotCreatedAndReadyAndInProgress() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.start(HOST_ID);
            runningCrew.end(HOST_ID);

            assertThatThrownBy(() -> runningCrew.accept(HOST_ID, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 완료되었습니다.");
        }
    }

    @DisplayName("참여 요청 거절 테스트")
    @Nested
    public class reject {

        private static final Long HOST_ID = 0L;

        @DisplayName("참여 요청을 거절한다")
        @Test
        void success() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.participate(memberId);

            runningCrew.reject(HOST_ID, memberId);

            final Optional<Participant> participant = runningCrew.getParticipants()
                .getValue()
                .stream()
                .filter(value -> value.getMemberId().equals(memberId))
                .findFirst();
            assertThat(participant).isPresent();
            assertThat(participant.get().getStatus()).isEqualTo(ParticipantType.REJECTED);
        }

        @DisplayName("승인자가 호스트가 아닐 경우 예외가 발생한다")
        @Test
        void memberIsNotHost() {
            final long memberId = 1L;
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.reject(memberId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("호스트가 아닙니다.");
        }

        @DisplayName("호스트일 경우 예외가 발생한다")
        @Test
        void memberIsHost() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);

            assertThatThrownBy(() -> runningCrew.reject(HOST_ID, HOST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루의 호스트입니다.");
        }
    }
}
