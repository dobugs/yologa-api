package com.dobugs.yologaapi.service;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static org.assertj.core.api.Assertions.assertThat;
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

import com.dobugs.yologaapi.domain.runningcrew.Participant;
import com.dobugs.yologaapi.domain.runningcrew.ParticipantType;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.ParticipantRepository;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.repository.dto.response.ParticipantDto;
import com.dobugs.yologaapi.service.dto.request.PagingRequest;
import com.dobugs.yologaapi.service.dto.response.ParticipantResponse;
import com.dobugs.yologaapi.service.dto.response.ParticipantsResponse;
import com.dobugs.yologaapi.support.PagingGenerator;
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
    private ParticipantRepository participantRepository;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private PagingGenerator pagingGenerator;

    @BeforeEach
    void setUp() {
        participationService = new ParticipationService(runningCrewRepository, participantRepository, tokenGenerator, pagingGenerator);
    }

    private String createToken(final Long memberId, final String provider, final String token) {
        return Jwts.builder()
            .claim("memberId", memberId)
            .claim("provider", provider)
            .claim("token", token)
            .compact();
    }

    @DisplayName("러닝크루 참여자 목록 조회 테스트")
    @Nested
    public class findParticipants {

        @DisplayName("러닝크루 참여자의 목록을 조회한다")
        @Test
        void success() {
            final long runningCrewId = 0L;
            final PagingRequest request = new PagingRequest(0, 10);

            final long memberId1 = 1L;
            final long memberId2 = 2L;
            final List<ParticipantDto> participantDtos = List.of(new ParticipantDtoImpl(memberId1, "유콩"), new ParticipantDtoImpl(memberId2, "건"));
            final Page<ParticipantDto> page = mock(Page.class);
            given(page.getTotalElements()).willReturn(0L);
            given(page.getNumber()).willReturn(0);
            given(page.getSize()).willReturn(0);
            given(page.getContent()).willReturn(participantDtos);
            given(participantRepository.findParticipants(eq(runningCrewId), eq(ParticipantType.PARTICIPATING.getSavedName()), any())).willReturn(page);

            final ParticipantsResponse response = participationService.findParticipants(runningCrewId, request);

            final List<Long> idsOfParticipants = response.content().stream()
                .map(ParticipantResponse::id)
                .toList();
            assertThat(idsOfParticipants).containsExactly(memberId1, memberId2);
        }

        private class ParticipantDtoImpl implements ParticipantDto {

            private final Long id;
            private final String nickname;

            public ParticipantDtoImpl(final Long id, final String nickname) {
                this.id = id;
                this.nickname = nickname;
            }

            @Override
            public Long getId() {
                return id;
            }

            @Override
            public String getNickname() {
                return nickname;
            }
        }
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
