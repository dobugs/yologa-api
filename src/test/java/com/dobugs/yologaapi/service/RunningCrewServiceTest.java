package com.dobugs.yologaapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.service.dto.common.CoordinatesDto;
import com.dobugs.yologaapi.service.dto.common.DateDto;
import com.dobugs.yologaapi.service.dto.common.LocationsDto;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("RunningCrew 서비스 테스트")
class RunningCrewServiceTest {

    private static final LocalDateTime AFTER_ONE_HOUR = LocalDateTime.now().plusHours(1);
    private static final LocalDateTime NOW = LocalDateTime.now();

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
            final RunningCrewCreateRequest request = createRunningCrewRequest();

            final RunningCrew savedRunningCrew = mock(RunningCrew.class);
            given(savedRunningCrew.getId()).willReturn(1L);
            given(runningCrewRepository.save(any())).willReturn(savedRunningCrew);

            final long runningCrewId = runningCrewService.create(request);

            assertThat(runningCrewId).isNotNull();
        }
    }

    private RunningCrewCreateRequest createRunningCrewRequest() {
        final CoordinatesDto coordinatesDto = new CoordinatesDto(123.456, 123.456);
        final LocationsDto locationsDto = new LocationsDto(coordinatesDto, coordinatesDto);
        final DateDto dateDto = new DateDto(NOW, AFTER_ONE_HOUR);

        return new RunningCrewCreateRequest(
            "title", locationsDto, 10, dateDto, AFTER_ONE_HOUR, "description"
        );
    }
}
