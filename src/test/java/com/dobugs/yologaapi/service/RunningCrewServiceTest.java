package com.dobugs.yologaapi.service;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.LATITUDE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.LONGITUDE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createMockRunningCrew;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewCreateRequest;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import com.dobugs.yologaapi.domain.runningcrew.ParticipantType;
import com.dobugs.yologaapi.domain.runningcrew.ProgressionType;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.service.dto.request.PagingRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewFindNearbyRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewStatusRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;
import com.dobugs.yologaapi.service.dto.response.RunningCrewsResponse;
import com.dobugs.yologaapi.support.PagingGenerator;
import com.dobugs.yologaapi.support.TokenGenerator;
import com.dobugs.yologaapi.support.dto.response.UserTokenResponse;

import io.jsonwebtoken.Jwts;

@ExtendWith(MockitoExtension.class)
@DisplayName("RunningCrew 서비스 테스트")
class RunningCrewServiceTest {

    private static final Long MEMBER_ID = 0L;
    private static final String PROVIDER = "google";
    private static final String ACCESS_TOKEN = "accessToken";

    private RunningCrewService runningCrewService;

    @Mock
    private RunningCrewRepository runningCrewRepository;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private PagingGenerator pagingGenerator;

    @BeforeEach
    void setUp() {
        runningCrewService = new RunningCrewService(runningCrewRepository, tokenGenerator, pagingGenerator);
    }

    private String createToken(final Long memberId, final String provider, final String token) {
        return Jwts.builder()
            .claim("memberId", memberId)
            .claim("provider", provider)
            .claim("token", token)
            .compact();
    }

    @DisplayName("러닝크루 생성 테스트")
    @Nested
    public class create {

        @DisplayName("러닝크루를 생성한다")
        @Test
        void success() {
            final RunningCrewCreateRequest request = createRunningCrewCreateRequest();
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew savedRunningCrew = mock(RunningCrew.class);
            given(savedRunningCrew.getId()).willReturn(MEMBER_ID);
            given(runningCrewRepository.save(any())).willReturn(savedRunningCrew);

            final long runningCrewId = runningCrewService.create(serviceToken, request);

            assertThat(runningCrewId).isNotNull();
        }
    }

    @DisplayName("내 주변 러닝크루 목록 조회 테스트")
    @Nested
    public class findNearby {

        @DisplayName("내 주변 러닝크루의 목록을 조회한다")
        @Test
        void success() {
            final RunningCrewFindNearbyRequest request = new RunningCrewFindNearbyRequest(
                LATITUDE, LONGITUDE, 3000, 0, 10
            );

            final Page<RunningCrew> page = mock(Page.class);
            given(page.getTotalElements()).willReturn(0L);
            given(page.getNumber()).willReturn(10);
            given(page.getSize()).willReturn(0);

            given(runningCrewRepository.findNearby(any(), any(), eq(3000), any())).willReturn(page);

            assertThatCode(() -> runningCrewService.findNearby(request))
                .doesNotThrowAnyException();
        }
    }

    @DisplayName("현재 진행중인 내 러닝크루 목록 조회 테스트")
    @Nested
    public class findInProgress {

        @DisplayName("현재 진행중인 내 러닝크루 목록을 조회한다")
        @Test
        void success() {
            final PagingRequest request = new PagingRequest(0, 10);
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final List<RunningCrew> runningCrews = List.of(createRunningCrew(MEMBER_ID));
            final Page<RunningCrew> page = mock(Page.class);
            given(page.getTotalElements()).willReturn(0L);
            given(page.getNumber()).willReturn(10);
            given(page.getSize()).willReturn(0);
            given(page.getContent()).willReturn(runningCrews);

            given(runningCrewRepository.findInProgress(eq(MEMBER_ID), eq(ProgressionType.IN_PROGRESS.getSavedName()),
                eq(ParticipantType.PARTICIPATING.getSavedName()), any())).willReturn(page);

            final RunningCrewsResponse response = runningCrewService.findInProgress(serviceToken, request);
            final List<Long> memberIdsOfResponse = response.content().stream()
                .map(RunningCrewResponse::host)
                .toList();

            assertThat(memberIdsOfResponse).contains(MEMBER_ID);
        }
    }

    @DisplayName("내가 주최한 러닝크루 목록 조회")
    @Nested
    public class findHosted {

        @DisplayName("내가 주최한 러닝크루 목록을 조회한다")
        @Test
        void success() {
            final String status = "CREATED";

            final RunningCrewStatusRequest request = new RunningCrewStatusRequest(status, 0, 10);
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final List<RunningCrew> runningCrews = List.of(createRunningCrew(MEMBER_ID));
            final Page<RunningCrew> page = mock(Page.class);
            given(page.getTotalElements()).willReturn(0L);
            given(page.getNumber()).willReturn(10);
            given(page.getSize()).willReturn(0);
            given(page.getContent()).willReturn(runningCrews);
            given(runningCrewRepository.findByMemberIdAndStatusAndArchivedIsTrue(eq(MEMBER_ID), eq(ProgressionType.CREATED), any()))
                .willReturn(page);

            final RunningCrewsResponse response = runningCrewService.findHosted(serviceToken, request);
            final List<Long> memberIdsOfResponse = response.content().stream()
                .map(RunningCrewResponse::host)
                .toList();

            assertThat(memberIdsOfResponse).contains(MEMBER_ID);
        }

        @DisplayName("러닝크루 상태값에 null 을 입력하면 모든 상태값의 러닝크루 목록을 조회한다")
        @Test
        void statusIsNull() {
            final String status = null;

            final RunningCrewStatusRequest request = new RunningCrewStatusRequest(status, 0, 10);
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final List<RunningCrew> runningCrews = List.of(createRunningCrew(MEMBER_ID));
            final Page<RunningCrew> page = mock(Page.class);
            given(page.getTotalElements()).willReturn(0L);
            given(page.getNumber()).willReturn(10);
            given(page.getSize()).willReturn(0);
            given(page.getContent()).willReturn(runningCrews);
            given(runningCrewRepository.findByMemberIdAndArchivedIsTrue(eq(MEMBER_ID), any())).willReturn(page);

            final RunningCrewsResponse response = runningCrewService.findHosted(serviceToken, request);
            final List<Long> memberIdsOfResponse = response.content().stream()
                .map(RunningCrewResponse::host)
                .toList();

            assertThat(memberIdsOfResponse).contains(MEMBER_ID);
        }

        @DisplayName("잘못된 상태값을 입력하면 예외가 발생한다")
        @Test
        void statusIsInvalid() {
            final String invalidStatus = "invalidStatus";

            final RunningCrewStatusRequest request = new RunningCrewStatusRequest(invalidStatus, 0, 10);
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            assertThatThrownBy(() -> runningCrewService.findHosted(serviceToken, request))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("내가 참여한 러닝크루 목록 조회")
    @Nested
    public class findParticipated {

        private static final Long HOST_ID = -1L;

        @DisplayName("내가 참여한 러닝크루 목록을 조회한다")
        @Test
        void success() {
            final String status = "CREATED";

            final RunningCrewStatusRequest request = new RunningCrewStatusRequest(status, 0, 10);
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final List<RunningCrew> runningCrews = List.of(createRunningCrew(HOST_ID));
            final Page<RunningCrew> page = mock(Page.class);
            given(page.getTotalElements()).willReturn(0L);
            given(page.getNumber()).willReturn(10);
            given(page.getSize()).willReturn(0);
            given(page.getContent()).willReturn(runningCrews);
            given(runningCrewRepository.findParticipatedByStatus(
                eq(MEMBER_ID), eq(ProgressionType.CREATED.getSavedName()), eq(ParticipantType.PARTICIPATING.getSavedName()), any())
            ).willReturn(page);

            final RunningCrewsResponse response = runningCrewService.findParticipated(serviceToken, request);
            final List<Long> memberIdsOfResponse = response.content().stream()
                .map(RunningCrewResponse::host)
                .toList();

            assertThat(memberIdsOfResponse).contains(HOST_ID);
        }

        @DisplayName("러닝크루 상태값에 null 을 입력하면 모든 상태값의 러닝크루 목록을 조회한다")
        @Test
        void statusIsNull() {
            final String status = null;

            final RunningCrewStatusRequest request = new RunningCrewStatusRequest(status, 0, 10);
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final List<RunningCrew> runningCrews = List.of(createRunningCrew(HOST_ID));
            final Page<RunningCrew> page = mock(Page.class);
            given(page.getTotalElements()).willReturn(0L);
            given(page.getNumber()).willReturn(10);
            given(page.getSize()).willReturn(0);
            given(page.getContent()).willReturn(runningCrews);
            given(runningCrewRepository.findParticipated(eq(MEMBER_ID), eq(ParticipantType.PARTICIPATING.getSavedName()), any())).willReturn(page);

            final RunningCrewsResponse response = runningCrewService.findParticipated(serviceToken, request);
            final List<Long> memberIdsOfResponse = response.content().stream()
                .map(RunningCrewResponse::host)
                .toList();

            assertThat(memberIdsOfResponse).contains(HOST_ID);
        }

        @DisplayName("잘못된 상태값을 입력하면 예외가 발생한다")
        @Test
        void statusIsInvalid() {
            final String invalidStatus = "invalidStatus";

            final RunningCrewStatusRequest request = new RunningCrewStatusRequest(invalidStatus, 0, 10);
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            assertThatThrownBy(() -> runningCrewService.findParticipated(serviceToken, request))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("러닝크루 상세정보 조회 테스트")
    @Nested
    public class findById {

        @DisplayName("러닝크루의 상세정보를 조회한다")
        @Test
        void success() {
            final long runningCrewId = 1L;

            final RunningCrew savedRunningCrew = createMockRunningCrew();
            given(savedRunningCrew.getId()).willReturn(runningCrewId);
            given(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).willReturn(Optional.of(savedRunningCrew));

            final RunningCrewResponse response = runningCrewService.findById(runningCrewId);

            assertThat(response.id()).isEqualTo(runningCrewId);
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
    public class update {

        @DisplayName("러닝크루를 수정한다")
        @Test
        void success() {
            final long runningCrewId = 0L;
            final RunningCrewUpdateRequest request = createRunningCrewUpdateRequest();
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew savedRunningCrew = mock(RunningCrew.class);
            given(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).willReturn(Optional.of(savedRunningCrew));

            assertThatCode(() -> runningCrewService.update(serviceToken, runningCrewId, request))
                .doesNotThrowAnyException();
        }

        @DisplayName("존재하지 않는 아이디로 러닝크루를 수정할 수 없다")
        @Test
        void isShouldExist() {
            final long notExistRunningCrewId = 0L;
            final RunningCrewUpdateRequest request = createRunningCrewUpdateRequest();
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            assertThatThrownBy(() -> runningCrewService.update(serviceToken, notExistRunningCrewId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }
    }

    @DisplayName("러닝크루 삭제 테스트")
    @Nested
    public class delete {

        @DisplayName("러닝크루를 삭제한다")
        @Test
        void success() {
            final long runningCrewId = 1L;
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew savedRunningCrew = createRunningCrew(MEMBER_ID);
            given(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).willReturn(Optional.of(savedRunningCrew));

            runningCrewService.delete(serviceToken, runningCrewId);

            assertThat(savedRunningCrew.isArchived()).isFalse();
        }

        @DisplayName("존재하지 않는 아이디로 러닝크루를 삭제할 수 없다")
        @Test
        void isShouldExist() {
            final long notExistId = 0L;
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            assertThatThrownBy(() -> runningCrewService.delete(serviceToken, notExistId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }
    }

    @DisplayName("러닝크루 시작 테스트")
    @Nested
    public class start {

        @DisplayName("러닝크루를 시작한다")
        @Test
        void success() {
            final long runningCrewId = 1L;
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew savedRunningCrew = createRunningCrew(MEMBER_ID);
            given(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).willReturn(Optional.of(savedRunningCrew));

            runningCrewService.start(serviceToken, runningCrewId);

            assertAll(
                () -> assertThat(savedRunningCrew.getImplementedStartDate()).isNotNull(),
                () -> assertThat(savedRunningCrew.getStatus()).isEqualTo(ProgressionType.IN_PROGRESS)
            );
        }

        @DisplayName("존재하지 않는 아이디로 러닝크루를 시작할 수 없다")
        @Test
        void isShouldExist() {
            final long notExistId = 0L;
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            assertThatThrownBy(() -> runningCrewService.start(serviceToken, notExistId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }
    }

    @DisplayName("러닝크루 종료 테스트")
    @Nested
    public class end {

        @DisplayName("러닝크루를 종료한다")
        @Test
        void success() {
            final long runningCrewId = 1L;
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew savedRunningCrew = createRunningCrew(MEMBER_ID);
            given(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).willReturn(Optional.of(savedRunningCrew));
            runningCrewService.start(serviceToken, runningCrewId);

            runningCrewService.end(serviceToken, runningCrewId);

            assertAll(
                () -> assertThat(savedRunningCrew.getImplementedEndDate()).isNotNull(),
                () -> assertThat(savedRunningCrew.getStatus()).isEqualTo(ProgressionType.COMPLETED)
            );
        }

        @DisplayName("존재하지 않는 아이디로 러닝크루를 종료할 수 없다")
        @Test
        void isShouldExist() {
            final long notExistId = 0L;
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            assertThatThrownBy(() -> runningCrewService.end(serviceToken, notExistId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }
    }
}
