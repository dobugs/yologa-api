package com.dobugs.yologaapi.service;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dobugs.yologaapi.domain.runningcrew.Participant;
import com.dobugs.yologaapi.domain.runningcrew.ParticipantType;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.support.TokenGenerator;
import com.dobugs.yologaapi.support.dto.response.UserTokenResponse;

import io.jsonwebtoken.Jwts;

@ExtendWith(MockitoExtension.class)
@DisplayName("Participation 서비스 테스트")
class ParticipationServiceTest {

    private static final Long MEMBER_ID = 0L;
    private static final String PROVIDER = "google";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final Long HOST_ID = 1L;

    private ParticipationService participationService;

    @Mock
    private RunningCrewRepository runningCrewRepository;

    @Mock
    private TokenGenerator tokenGenerator;

    @BeforeEach
    void setUp() {
        participationService = new ParticipationService(runningCrewRepository, tokenGenerator);
    }

    private String createToken(final Long memberId, final String provider, final String token) {
        return Jwts.builder()
            .claim("memberId", memberId)
            .claim("provider", provider)
            .claim("token", token)
            .compact();
    }

    @DisplayName("참여 요청 테스트")
    @Nested
    public class participate {

        @DisplayName("러닝크루에 참여 요청을 한다")
        @Test
        void success() {
            final long runningCrewId = 0L;

            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            given(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).willReturn(Optional.of(runningCrew));

            participationService.participate(serviceToken, runningCrewId);

            final Optional<Participant> participant = runningCrew.getParticipants()
                .getValue()
                .stream()
                .filter(value -> value.getMemberId().equals(MEMBER_ID))
                .findFirst();
            assertThat(participant).isPresent();
            assertThat(participant.get().getStatus()).isEqualTo(ParticipantType.REQUESTED);
        }
    }

    @DisplayName("참여 요청 취소 테스트")
    @Nested
    public class cancel {

        @DisplayName("러닝크루에 참여 요청 취소를 한다")
        @Test
        void success() {
            final long runningCrewId = 0L;

            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(serviceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            given(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).willReturn(Optional.of(runningCrew));

            participationService.participate(serviceToken, runningCrewId);
            participationService.cancel(serviceToken, runningCrewId);

            final Optional<Participant> participant = runningCrew.getParticipants()
                .getValue()
                .stream()
                .filter(value -> value.getMemberId().equals(MEMBER_ID))
                .findFirst();
            assertThat(participant).isPresent();
            assertThat(participant.get().getStatus()).isEqualTo(ParticipantType.CANCELLED);
        }
    }

    @DisplayName("탈퇴 테스트")
    @Nested
    public class withdraw {

        @DisplayName("러닝크루에 탈퇴한다")
        @Test
        void success() {
            final long runningCrewId = 0L;

            final String memberServiceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final String hostServiceToken = createToken(HOST_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(memberServiceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));
            given(tokenGenerator.extract(hostServiceToken)).willReturn(new UserTokenResponse(HOST_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            given(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).willReturn(Optional.of(runningCrew));

            participationService.participate(memberServiceToken, runningCrewId);
            participationService.accept(hostServiceToken, runningCrewId, MEMBER_ID);
            participationService.withdraw(memberServiceToken, runningCrewId);

            final Optional<Participant> participant = runningCrew.getParticipants()
                .getValue()
                .stream()
                .filter(value -> value.getMemberId().equals(MEMBER_ID))
                .findFirst();
            assertThat(participant).isPresent();
            assertThat(participant.get().getStatus()).isEqualTo(ParticipantType.WITHDRAWN);
        }
    }

    @DisplayName("참여 요청 승인 테스트")
    @Nested
    public class accept {

        @DisplayName("러닝크루 참여 요청을 승인한다")
        @Test
        void success() {
            final long runningCrewId = 0L;

            final String memberServiceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final String hostServiceToken = createToken(HOST_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(memberServiceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));
            given(tokenGenerator.extract(hostServiceToken)).willReturn(new UserTokenResponse(HOST_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            given(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).willReturn(Optional.of(runningCrew));

            participationService.participate(memberServiceToken, runningCrewId);
            participationService.accept(hostServiceToken, runningCrewId, MEMBER_ID);

            final Optional<Participant> participant = runningCrew.getParticipants()
                .getValue()
                .stream()
                .filter(value -> value.getMemberId().equals(MEMBER_ID))
                .findFirst();
            assertThat(participant).isPresent();
            assertThat(participant.get().getStatus()).isEqualTo(ParticipantType.PARTICIPATING);
        }
    }

    @DisplayName("참여 요청 거절 테스트")
    @Nested
    public class reject {

        @DisplayName("러닝크루 참여 요청을 거절한다")
        @Test
        void success() {
            final long runningCrewId = 0L;

            final String memberServiceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN);
            final String hostServiceToken = createToken(HOST_ID, PROVIDER, ACCESS_TOKEN);
            given(tokenGenerator.extract(memberServiceToken)).willReturn(new UserTokenResponse(MEMBER_ID, PROVIDER, ACCESS_TOKEN));
            given(tokenGenerator.extract(hostServiceToken)).willReturn(new UserTokenResponse(HOST_ID, PROVIDER, ACCESS_TOKEN));

            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            given(runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)).willReturn(Optional.of(runningCrew));

            participationService.participate(memberServiceToken, runningCrewId);
            participationService.reject(hostServiceToken, runningCrewId, MEMBER_ID);

            final Optional<Participant> participant = runningCrew.getParticipants()
                .getValue()
                .stream()
                .filter(value -> value.getMemberId().equals(MEMBER_ID))
                .findFirst();
            assertThat(participant).isPresent();
            assertThat(participant.get().getStatus()).isEqualTo(ParticipantType.REJECTED);
        }
    }
}
