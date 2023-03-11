package com.dobugs.yologaapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dobugs.yologaapi.auth.dto.response.ServiceToken;
import com.dobugs.yologaapi.domain.runningcrew.ParticipantType;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.ParticipantRepository;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.repository.dto.response.ParticipantDto;
import com.dobugs.yologaapi.service.dto.request.PagingRequest;
import com.dobugs.yologaapi.service.dto.response.ParticipantsResponse;
import com.dobugs.yologaapi.support.PagingGenerator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ParticipationService {

    private final RunningCrewRepository runningCrewRepository;
    private final ParticipantRepository participantRepository;
    private final PagingGenerator pagingGenerator;

    @Transactional(readOnly = true)
    public ParticipantsResponse findParticipants(final Long runningCrewId, final PagingRequest request) {
        final Pageable pageable = pagingGenerator.from(request.page(), request.size());
        final Page<ParticipantDto> response = participantRepository.findParticipants(runningCrewId, ParticipantType.PARTICIPATING.getSavedName(), pageable);
        return ParticipantsResponse.from(response);
    }

    public void participate(final ServiceToken serviceToken, final Long runningCrewId) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.participate(serviceToken.memberId());
    }

    public void cancel(final ServiceToken serviceToken, final Long runningCrewId) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.cancel(serviceToken.memberId());
    }

    public void withdraw(final ServiceToken serviceToken, final Long runningCrewId) {
        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.withdraw(serviceToken.memberId());
    }

    public void accept(final ServiceToken serviceToken, final Long runningCrewId, final Long memberId) {
        final Long hostId = serviceToken.memberId();

        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.accept(hostId, memberId);
    }

    public void reject(final ServiceToken serviceToken, final Long runningCrewId, final Long memberId) {
        final Long hostId = serviceToken.memberId();

        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.reject(hostId, memberId);
    }

    private RunningCrew findRunningCrewBy(final Long runningCrewId) {
        return runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("러닝크루가 존재하지 않습니다. [%d]", runningCrewId)));
    }
}
