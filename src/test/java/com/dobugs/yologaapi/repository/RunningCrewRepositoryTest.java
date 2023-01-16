package com.dobugs.yologaapi.repository;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.LATITUDE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.LONGITUDE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static org.assertj.core.api.Assertions.assertThat;

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

    @DisplayName("아이디와 삭제 여부를 통해서 러닝크루를 조회한다")
    @Nested
    public class findByIdAndArchivedTest {

        @DisplayName("삭제되지 않은 러닝크루를 조회한다")
        @Test
        void archivedIsTrue() {
            final boolean archived = true;

            final RunningCrew runningCrew = createRunningCrew();
            runningCrewRepository.save(runningCrew);

            final Optional<RunningCrew> actual = runningCrewRepository.findByIdAndArchived(runningCrew.getId(), archived);

            assertThat(actual).isPresent();
        }

        @DisplayName("삭제된 러닝크루를 조회한다")
        @Test
        void archivedIsFalse() {
            final boolean archived = false;

            final RunningCrew runningCrew = createRunningCrew();
            final RunningCrew savedRunningCrew = runningCrewRepository.save(runningCrew);
            savedRunningCrew.delete();
            entityManager.flush();

            final Optional<RunningCrew> actual = runningCrewRepository.findByIdAndArchived(runningCrew.getId(), archived);

            assertThat(actual).isPresent();
        }
    }

    @DisplayName("내 주변에 있는 러닝크루 목록을 조회한다")
    @Nested
    public class findNearbyTest {

        @DisplayName("내 주변에 있는 러닝크루 목록을 조회한다")
        @Test
        void findNearby() {
            final int count = 3;

            for (int i = 0; i < count; i++) {
                runningCrewRepository.save(createRunningCrew());
            }

            final Pageable pageable = PageRequest.of(0, 5);
            final Page<RunningCrew> runningCrews = runningCrewRepository.findNearby(LATITUDE, LONGITUDE, 100, pageable);

            assertThat(runningCrews).hasSizeGreaterThan(count);
        }
    }
}
