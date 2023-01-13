package com.dobugs.yologaapi.service;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createMockRunningCrew;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewCreateRequest;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("RunningCrew 서비스 테스트")
class RunningCrewServiceTest {

    @Mock
    private RunningCrewRepository runningCrewRepository;

    private RunningCrewService runningCrewService;

    @BeforeEach
    void setUp() {
        runningCrewService = new RunningCrewService(runningCrewRepository);
    }

    @DisplayName("러닝크루 생성 테스트")
    @Nested
    public class createTest {

        @DisplayName("러닝크루를 생성한다")
        @Test
        void create() {
            final RunningCrewCreateRequest request = createRunningCrewCreateRequest();

            final RunningCrew savedRunningCrew = mock(RunningCrew.class);
            given(savedRunningCrew.getId()).willReturn(1L);
            given(runningCrewRepository.save(any())).willReturn(savedRunningCrew);

            final long runningCrewId = runningCrewService.create(request);

            assertThat(runningCrewId).isNotNull();
        }
    }

    @DisplayName("러닝크루 상세정보 조회 테스트")
    @Nested
    public class findByIdTest {

        @DisplayName("러닝크루의 상세정보를 조회한다")
        @Test
        void findById() {
            final long runningCrewId = 1L;

            final RunningCrew savedRunningCrew = createMockRunningCrew();
            given(savedRunningCrew.getId()).willReturn(runningCrewId);
            given(runningCrewRepository.findById(runningCrewId)).willReturn(Optional.of(savedRunningCrew));

            final RunningCrewResponse response = runningCrewService.findById(runningCrewId);

            assertThat(response.getId()).isEqualTo(runningCrewId);
        }

        @DisplayName("존재하지 않는 아이디로 러닝크루의 상세정보를 조회할 수 없다")
        @Test
        void idShouldExist() {
            final long notExistId = 0L;

            assertThatThrownBy(() -> runningCrewService.findById(notExistId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }
    }

    @DisplayName("러닝크루 수정 테스트")
    @Nested
    public class updateTest {

        @DisplayName("러닝크루를 수정한다")
        @Test
        void update() {
            final long runningCrewId = 1L;
            final RunningCrewUpdateRequest request = createRunningCrewUpdateRequest();

            final RunningCrew savedRunningCrew = mock(RunningCrew.class);
            given(runningCrewRepository.findById(runningCrewId)).willReturn(Optional.of(savedRunningCrew));

            assertThatCode(() -> runningCrewService.update(runningCrewId, request))
                .doesNotThrowAnyException();
        }

        @DisplayName("존재하지 않는 아이디로 러닝크루를 수정할 수 없다")
        @Test
        void isShouldExist() {
            final long notExistId = 0L;
            final RunningCrewUpdateRequest request = createRunningCrewUpdateRequest();

            assertThatThrownBy(() -> runningCrewService.update(notExistId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }
    }

    @DisplayName("러닝크루 삭제 테스트")
    @Nested
    public class deleteTest {

        @DisplayName("러닝크루를 삭제한다")
        @Test
        void delete() {
            final long runningCrewId = 1L;

            runningCrewService.delete(runningCrewId);

            assertThatThrownBy(() -> runningCrewService.findById(runningCrewId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }
    }
}
