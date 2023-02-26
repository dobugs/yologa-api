package com.dobugs.yologaapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.support.TokenGenerator;
import com.dobugs.yologaapi.support.dto.response.UserTokenResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ParticipationService {

    private final RunningCrewRepository runningCrewRepository;
    private final TokenGenerator tokenGenerator;

    public void participate(final String serviceToken, final Long runningCrewId) {
        final UserTokenResponse userTokenResponse = tokenGenerator.extract(serviceToken);
        final Long memberId = userTokenResponse.memberId();

        final RunningCrew savedRunningCrew = findRunningCrewBy(runningCrewId);
        savedRunningCrew.participate(memberId);
    }

    private RunningCrew findRunningCrewBy(final Long runningCrewId) {
        return runningCrewRepository.findByIdAndArchivedIsTrue(runningCrewId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("러닝크루가 존재하지 않습니다. [%d]", runningCrewId)));
    }
}
