package com.dobugs.yologaapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dobugs.yologaapi.domain.runningcrew.ParticipantType;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.ParticipantRepository;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.repository.dto.response.ParticipantDto;
import com.dobugs.yologaapi.service.dto.response.ParticipantResponse;
import com.dobugs.yologaapi.service.dto.response.ParticipantsResponse;
import com.dobugs.yologaapi.support.TokenGenerator;
import com.dobugs.yologaapi.support.dto.response.UserTokenResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ParticipationService {

    private final RunningCrewRepository runningCrewRepository;
    private final ParticipantRepository participantRepository;
    private final TokenGenerator tokenGenerator;

    @Transactional(readOnly = true)
    public ParticipantsResponse findParticipants(final Long runningCrewId) {
        final List<ParticipantDto> response = participantRepository.findParticipants(runningCrewId, ParticipantType.PARTICIPATING.getSavedName());
        return new ParticipantsResponse(
            response.stream()
                .map(participant -> new ParticipantResponse(participant.getId(), participant.getNickname()))
                .toList()
        );
    }

    public void participate(final String serviceToken, final Long runningCrewId) {
        final UserTokenResponse userTokenResponse = tokenGenerator.extract(serviceToken);
        final Long memberId = userTokenResponse.memberId();

        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.participate(memberId);
    }

    public void cancel(final String serviceToken, final Long runningCrewId) {
        final UserTokenResponse userTokenResponse = tokenGenerator.extract(serviceToken);
        final Long memberId = userTokenResponse.memberId();

        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.cancel(memberId);
    }

    public void withdraw(final String serviceToken, final Long runningCrewId) {
        final UserTokenResponse userTokenResponse = tokenGenerator.extract(serviceToken);
        final Long memberId = userTokenResponse.memberId();

        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.withdraw(memberId);
    }

    public void accept(final String serviceToken, final Long runningCrewId, final Long memberId) {
        final UserTokenResponse userTokenResponse = tokenGenerator.extract(serviceToken);
        final Long hostId = userTokenResponse.memberId();

        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.accept(hostId, memberId);
    }

    public void reject(final String serviceToken, final Long runningCrewId, final Long memberId) {
        final UserTokenResponse userTokenResponse = tokenGenerator.extract(serviceToken);
        final Long hostId = userTokenResponse.memberId();

        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.reject(hostId, memberId);
    }

    private RunningCrew findRunningCrewBy(final Long runningCrewId) {
        return runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("러닝크루가 존재하지 않습니다. [%d]", runningCrewId)));
    }
}
