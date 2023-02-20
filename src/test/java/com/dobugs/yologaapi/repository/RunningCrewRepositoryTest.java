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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@DisplayName("RunningCrew 레포지토리 테스트")
class RunningCrewRepositoryTest {

    @Autowired
    private RunningCrewRepository runningCrewRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("러닝크루 아이디를 이용하여 러닝크루 정보를 조회하는 테스트")
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
            final RunningCrew savedRunningCrew = runningCrewRepository.save(runningCrew);
            savedRunningCrew.delete(HOST_ID);
            entityManager.flush();

            assertAll(
                () -> assertThat(runningCrewRepository.findById(savedRunningCrew.getId())).isPresent(),
                () -> assertThat(runningCrewRepository.findByIdAndArchivedIsTrue(savedRunningCrew.getId())).isEmpty()
            );
        }

        @DisplayName("RunningCrew 가 존재하지 않을 경우 RunningCrew 를 조회하지 못한다")
        @Test
        void notExist() {
            final long runningCrewId = 0L;

            assertAll(
                () -> assertThat(runningCrewRepository.findById(runningCrewId)).isEmpty(),
                () -> assertThat(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).isEmpty()
            );
        }
    }

    @DisplayName("내 주변에 있는 러닝크루 목록을 조회한다")
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

            final Pageable pageable = PageRequest.of(0, 5);
            final Page<RunningCrew> runningCrews = runningCrewRepository.findNearby(LATITUDE, LONGITUDE, 100, pageable);

            assertThat(runningCrews).hasSizeGreaterThanOrEqualTo(count);
        }
    }
}
