package com.dobugs.yologaapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dobugs.yologaapi.domain.runningcrew.Capacity;
import com.dobugs.yologaapi.domain.runningcrew.Deadline;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrewProgression;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.service.dto.common.CoordinatesDto;
import com.dobugs.yologaapi.service.dto.common.DateDto;
import com.dobugs.yologaapi.service.dto.common.LocationsDto;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("RunningCrew 서비스 테스트")
class RunningCrewServiceTest {

    private static final double LATITUDE = 123.456;
    private static final double LONGITUDE = 123.456;

    private static final String RUNNING_CREW_TITLE = "title";
    private static final int RUNNING_CREW_CAPACITY = 10;
    private static final String RUNNING_CREW_DESCRIPTION = "description";

    private static final CoordinatesDto COORDINATES_DTO = new CoordinatesDto(LATITUDE, LONGITUDE);
    private static final LocationsDto LOCATIONS_DTO = new LocationsDto(COORDINATES_DTO, COORDINATES_DTO);

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime AFTER_ONE_HOUR = LocalDateTime.now().plusHours(1);
    private static final DateDto DATE_DTO = new DateDto(NOW, AFTER_ONE_HOUR);

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

    private RunningCrewCreateRequest createRunningCrewCreateRequest() {
        return new RunningCrewCreateRequest(
            RUNNING_CREW_TITLE, LOCATIONS_DTO, RUNNING_CREW_CAPACITY, DATE_DTO, AFTER_ONE_HOUR, RUNNING_CREW_DESCRIPTION
        );
    }

    private RunningCrewUpdateRequest createRunningCrewUpdateRequest() {
        return new RunningCrewUpdateRequest(
            RUNNING_CREW_TITLE, LOCATIONS_DTO, RUNNING_CREW_CAPACITY, DATE_DTO, AFTER_ONE_HOUR, RUNNING_CREW_DESCRIPTION
        );
    }

    private RunningCrew createMockRunningCrew() {
        final Point point = mock(Point.class);
        given(point.getX()).willReturn(LATITUDE);
        given(point.getY()).willReturn(LONGITUDE);

        final RunningCrew runningCrew = mock(RunningCrew.class);
        given(runningCrew.getDeparture()).willReturn(point);
        given(runningCrew.getArrival()).willReturn(point);
        given(runningCrew.getStatus()).willReturn(RunningCrewProgression.CREATED);
        given(runningCrew.getCapacity()).willReturn(new Capacity(RUNNING_CREW_CAPACITY));
        given(runningCrew.getDeadline()).willReturn(new Deadline(AFTER_ONE_HOUR));

        return runningCrew;
    }
}
