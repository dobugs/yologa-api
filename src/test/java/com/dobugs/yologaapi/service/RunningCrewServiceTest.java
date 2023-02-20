package com.dobugs.yologaapi.service;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.LATITUDE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.LONGITUDE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createMockRunningCrew;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewCreateRequest;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.data.domain.Page;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewFindNearbyRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;
import com.dobugs.yologaapi.support.TokenGenerator;
import com.dobugs.yologaapi.support.dto.response.UserTokenResponse;

import io.jsonwebtoken.Jwts;

@ExtendWith(MockitoExtension.class)
@DisplayName("RunningCrew 서비스 테스트")
class RunningCrewServiceTest {

    private RunningCrewService runningCrewService;

    @Mock
    private RunningCrewRepository runningCrewRepository;

    @Mock
    private TokenGenerator tokenGenerator;

    @BeforeEach
    void setUp() {
        runningCrewService = new RunningCrewService(runningCrewRepository, tokenGenerator);
    }

    @DisplayName("러닝크루 생성 테스트")
    @Nested
    public class create {

        private static final Long MEMBER_ID = 0L;
        private static final String PROVIDER = "google";
        private static final String ACCESS_TOKEN = "accessToken";

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

        private String createToken(final Long memberId, final String provider, final String token) {
            return Jwts.builder()
                .claim("memberId", memberId)
                .claim("provider", provider)
                .claim("token", token)
                .compact();
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
            given(page.getNumber()).willReturn(0);
            given(page.getSize()).willReturn(0);

            given(runningCrewRepository.findNearby(any(), any(), eq(3000), any())).willReturn(page);

            assertThatCode(() -> runningCrewService.findNearby(request))
                .doesNotThrowAnyException();
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
            given(runningCrewRepository.findByIdAndArchived(runningCrewId, true)).willReturn(Optional.of(savedRunningCrew));

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

        private static final Long MEMBER_ID = 0L;
        private static final String PROVIDER = "google";
        private static final String ACCESS_TOKEN = "accessToken";

        @DisplayName("러닝크루를 수정한다")
        @Test
        void success() {
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final long runningCrewId = 0L;
            final RunningCrewUpdateRequest request = createRunningCrewUpdateRequest();

            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew savedRunningCrew = mock(RunningCrew.class);
            given(runningCrewRepository.findByIdAndArchived(runningCrewId, true)).willReturn(Optional.of(savedRunningCrew));

            assertThatCode(() -> runningCrewService.update(serviceToken, runningCrewId, request))
                .doesNotThrowAnyException();
        }

        @DisplayName("존재하지 않는 아이디로 러닝크루를 수정할 수 없다")
        @Test
        void isShouldExist() {
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final long notExistRunningCrewId = 0L;
            final RunningCrewUpdateRequest request = createRunningCrewUpdateRequest();

            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            assertThatThrownBy(() -> runningCrewService.update(serviceToken, notExistRunningCrewId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }

        private String createToken(final Long memberId, final String provider, final String token) {
            return Jwts.builder()
                .claim("memberId", memberId)
                .claim("provider", provider)
                .claim("token", token)
                .compact();
        }
    }

    @DisplayName("러닝크루 삭제 테스트")
    @Nested
    public class delete {

        private static final Long MEMBER_ID = 0L;
        private static final String PROVIDER = "google";
        private static final String ACCESS_TOKEN = "accessToken";

        @DisplayName("러닝크루를 삭제한다")
        @Test
        void success() {
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final long runningCrewId = 1L;

            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew savedRunningCrew = mock(RunningCrew.class);
            given(runningCrewRepository.findByIdAndArchived(runningCrewId, true)).willReturn(Optional.of(savedRunningCrew));

            runningCrewService.delete(serviceToken, runningCrewId);

            given(runningCrewRepository.findByIdAndArchived(runningCrewId, true)).willReturn(Optional.empty());

            assertThatThrownBy(() -> runningCrewService.findById(runningCrewId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }

        @DisplayName("존재하지 않는 아이디로 러닝크루를 삭제할 수 없다")
        @Test
        void isShouldExist() {
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final long notExistId = 0L;

            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            assertThatThrownBy(() -> runningCrewService.delete(serviceToken, notExistId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }

        private String createToken(final Long memberId, final String provider, final String token) {
            return Jwts.builder()
                .claim("memberId", memberId)
                .claim("provider", provider)
                .claim("token", token)
                .compact();
        }
    }

    @DisplayName("러닝크루 시작 테스트")
    @Nested
    public class start {

        private static final Long MEMBER_ID = 0L;
        private static final String PROVIDER = "google";
        private static final String ACCESS_TOKEN = "accessToken";

        @DisplayName("러닝크루를 시작한다")
        @Test
        void success() {
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final long runningCrewId = 1L;

            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew savedRunningCrew = mock(RunningCrew.class);
            given(runningCrewRepository.findByIdAndArchived(runningCrewId, true)).willReturn(Optional.of(savedRunningCrew));

            assertThatCode(() -> runningCrewService.start(serviceToken, runningCrewId))
                .doesNotThrowAnyException();
        }

        @DisplayName("존재하지 않는 아이디로 러닝크루를 시작할 수 없다")
        @Test
        void isShouldExist() {
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final long notExistId = 0L;

            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            assertThatThrownBy(() -> runningCrewService.start(serviceToken, notExistId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }

        private String createToken(final Long memberId, final String provider, final String token) {
            return Jwts.builder()
                .claim("memberId", memberId)
                .claim("provider", provider)
                .claim("token", token)
                .compact();
        }
    }

    @DisplayName("러닝크루 종료 테스트")
    @Nested
    public class end {

        private static final Long MEMBER_ID = 0L;
        private static final String PROVIDER = "google";
        private static final String ACCESS_TOKEN = "accessToken";

        @DisplayName("러닝크루를 종료한다")
        @Test
        void success() {
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final long runningCrewId = 1L;

            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew savedRunningCrew = mock(RunningCrew.class);
            given(runningCrewRepository.findByIdAndArchived(runningCrewId, true)).willReturn(Optional.of(savedRunningCrew));

            assertThatCode(() -> runningCrewService.end(serviceToken, runningCrewId))
                .doesNotThrowAnyException();
        }

        @DisplayName("존재하지 않는 아이디로 러닝크루를 종료할 수 없다")
        @Test
        void isShouldExist() {
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final long notExistId = 0L;

            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            assertThatThrownBy(() -> runningCrewService.end(serviceToken, notExistId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("러닝크루가 존재하지 않습니다.");
        }

        private String createToken(final Long memberId, final String provider, final String token) {
            return Jwts.builder()
                .claim("memberId", memberId)
                .claim("provider", provider)
                .claim("token", token)
                .compact();
        }
    }
}
