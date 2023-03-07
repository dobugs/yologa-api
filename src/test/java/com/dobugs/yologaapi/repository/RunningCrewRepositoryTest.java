package com.dobugs.yologaapi.repository;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.LATITUDE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.LONGITUDE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dobugs.yologaapi.domain.runningcrew.ParticipantType;
import com.dobugs.yologaapi.domain.runningcrew.ProgressionType;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@DisplayName("RunningCrew 레포지토리 테스트")
class RunningCrewRepositoryTest {

    @Autowired
    private RunningCrewRepository runningCrewRepository;

    @DisplayName("러닝크루 아이디를 이용하여 러닝크루 정보 조회")
    @Nested
    public class findByIdAndArchivedIsTrue {

        private static final Long HOST_ID = 0L;

        @DisplayName("archived 가 true 일 경우 RunningCrew 를 조회한다")
        @Test
        void archivedIsTrue() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            final RunningCrew savedRunningCrew = runningCrewRepository.save(runningCrew);

            final Optional<RunningCrew> actual = runningCrewRepository.findByIdAndArchivedIsTrue(savedRunningCrew.getId());

            assertThat(actual).isPresent();
        }

        @DisplayName("archived 가 false 일 경우 RunningCrew 를 조회하지 못한다")
        @Test
        void archivedIsFalse() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.delete(HOST_ID);
            final RunningCrew savedRunningCrew = runningCrewRepository.save(runningCrew);

            assertAll(
                () -> assertThat(runningCrewRepository.findById(savedRunningCrew.getId())).isPresent(),
                () -> assertThat(runningCrewRepository.findByIdAndArchivedIsTrue(savedRunningCrew.getId())).isEmpty()
            );
        }

        @DisplayName("RunningCrew 가 존재하지 않을 경우 RunningCrew 를 조회하지 못한다")
        @Test
        void notExist() {
            final long notExistRunningCrewId = 0L;

            assertAll(
                () -> assertThat(runningCrewRepository.findById(notExistRunningCrewId)).isEmpty(),
                () -> assertThat(runningCrewRepository.findByIdAndArchivedIsTrue(notExistRunningCrewId)).isEmpty()
            );
        }
    }

    @DisplayName("내 주변 러닝크루 목록 조회")
    @Nested
    public class findNearby {

        private static final Long HOST_ID = 0L;

        @DisplayName("내 주변에 있는 러닝크루 목록을 조회한다")
        @Test
        void success() {
            final int count = 3;

            for (int i = 0; i < count; i++) {
                runningCrewRepository.save(createRunningCrew(HOST_ID));
            }

            final Pageable pageable = Pageable.unpaged();
            final Page<RunningCrew> runningCrews = runningCrewRepository.findNearby(LATITUDE, LONGITUDE, 100, pageable);

            assertThat(runningCrews).hasSizeGreaterThanOrEqualTo(count);
        }
    }

    @DisplayName("현재 진행중인 내 러닝크루 목록 조회")
    @Nested
    public class findInProgress {

        private static final Long HOST_ID = 0L;

        @DisplayName("현재 진행중인 내 러닝크루 목록을 조회한다")
        @Test
        void success() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.start(HOST_ID);
            final RunningCrew savedRunningCrew = runningCrewRepository.save(runningCrew);

            final Pageable pageable = Pageable.unpaged();
            final Page<RunningCrew> runningCrews = runningCrewRepository.findInProgress(
                HOST_ID, ProgressionType.IN_PROGRESS.getSavedName(), ParticipantType.PARTICIPATING.getSavedName(), pageable
            );

            assertThat(runningCrews).contains(savedRunningCrew);
        }
    }

    @DisplayName("주최자 아이디를 이용하여 러닝크루 정보 조회")
    @Nested
    public class findByMemberIdAndArchivedIsTrue {

        private static final Long HOST_ID = 0L;
        private static final Pageable pageable = Pageable.unpaged();

        @DisplayName("archived 가 true 일 경우 RunningCrew 를 조회한다")
        @Test
        void archivedIsTrue() {
            runningCrewRepository.save(createRunningCrew(HOST_ID));

            final Page<RunningCrew> runningCrews = runningCrewRepository.findByMemberIdAndArchivedIsTrue(HOST_ID, pageable);

            assertThat(runningCrews.getContent()).isNotEmpty();
        }

        @DisplayName("archived 가 false 일 경우 RunningCrew 를 조회하지 못한다")
        @Test
        void archivedIsFalse() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.delete(HOST_ID);
            runningCrewRepository.save(runningCrew);

            final Page<RunningCrew> runningCrews = runningCrewRepository.findByMemberIdAndArchivedIsTrue(HOST_ID, pageable);

            assertThat(runningCrews.getContent()).isEmpty();
        }

        @DisplayName("주최자 정보가 존재하지 않을 경우 RunningCrew 를 조회하지 못한다")
        @Test
        void notExist() {
            final long notExistHostId = -1L;

            final Page<RunningCrew> runningCrews = runningCrewRepository.findByMemberIdAndArchivedIsTrue(notExistHostId, pageable);

            assertThat(runningCrews.getContent()).isEmpty();
        }
    }

    @DisplayName("주최자 아이디와 러닝크루 상태값을 이용하여 러닝크루 정보 조회")
    @Nested
    public class findByMemberIdAndStatusAndArchivedIsTrue {

        private static final Long HOST_ID = 0L;
        private static final Pageable pageable = Pageable.unpaged();

        @DisplayName("archived 가 true 일 경우 RunningCrew 를 조회한다")
        @Test
        void archivedIsTrue() {
            runningCrewRepository.save(createRunningCrew(HOST_ID));

            final Page<RunningCrew> runningCrews = runningCrewRepository.findByMemberIdAndStatusAndArchivedIsTrue(HOST_ID, ProgressionType.CREATED, pageable);

            assertThat(runningCrews.getContent()).isNotEmpty();
        }

        @DisplayName("archived 가 false 일 경우 RunningCrew 를 조회하지 못한다")
        @Test
        void archivedIsFalse() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.delete(HOST_ID);
            runningCrewRepository.save(runningCrew);

            final Page<RunningCrew> runningCrews = runningCrewRepository.findByMemberIdAndStatusAndArchivedIsTrue(HOST_ID, ProgressionType.CREATED, pageable);

            assertThat(runningCrews.getContent()).isEmpty();
        }

        @DisplayName("주최자에 해당하는 정보가 않을 경우 RunningCrew 를 조회하지 못한다")
        @Test
        void notExistHost() {
            final long notExistHostId = -1L;

            final Page<RunningCrew> runningCrews = runningCrewRepository.findByMemberIdAndStatusAndArchivedIsTrue(notExistHostId, ProgressionType.CREATED, pageable);

            assertThat(runningCrews.getContent()).isEmpty();
        }

        @DisplayName("상태값에 해당하는 정보가 없을 경우 RunningCrew 를 조회하지 못한다")
        @Test
        void notExistStatus() {
            final ProgressionType notExistProgressionType = ProgressionType.EXPIRED;

            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.delete(HOST_ID);
            runningCrewRepository.save(runningCrew);

            final Page<RunningCrew> runningCrews = runningCrewRepository.findByMemberIdAndStatusAndArchivedIsTrue(HOST_ID, notExistProgressionType, pageable);

            assertThat(runningCrews.getContent()).isEmpty();
        }
    }

    @DisplayName("내가 참여한 러닝크루 목록 조회")
    @Nested
    public class findParticipated {

        private static final Long HOST_ID = 0L;

        @DisplayName("내가 참여한 러닝크루 목록을 조회한다")
        @Test
        void success() {
            final long memberId = -1L;

            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.participate(memberId);
            runningCrew.accept(HOST_ID, memberId);
            runningCrewRepository.save(runningCrew);

            final Pageable pageable = Pageable.unpaged();
            final Page<RunningCrew> runningCrews = runningCrewRepository.findParticipated(memberId, ParticipantType.PARTICIPATING.getSavedName(), pageable);

            assertThat(runningCrews.getContent()).isNotEmpty();
        }
    }

    @DisplayName("상태값을 이용하여 내가 참여한 러닝크루 목록 조회")
    @Nested
    public class findParticipatedByStatus {

        private static final Long HOST_ID = 0L;

        @DisplayName("상태값을 이용하여 내가 참여한 러닝크루 목록을 조회한다")
        @Test
        void success() {
            final long memberId = -1L;

            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            runningCrew.participate(memberId);
            runningCrew.accept(HOST_ID, memberId);
            runningCrewRepository.save(runningCrew);

            final Pageable pageable = Pageable.unpaged();
            final Page<RunningCrew> runningCrews = runningCrewRepository.findParticipatedByStatus(
                memberId, ProgressionType.READY.getSavedName(), ParticipantType.PARTICIPATING.getSavedName(), pageable
            );

            assertThat(runningCrews.getContent()).isNotEmpty();
        }
    }
}
