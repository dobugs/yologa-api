package com.dobugs.yologaapi.service.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;

public record RunningCrewFindNearbyResponse(long totalElements, int page, int size, List<RunningCrewSummaryResponse> content) {

    public static RunningCrewFindNearbyResponse from(final Page<RunningCrew> runningCrews) {
        return new RunningCrewFindNearbyResponse(
            runningCrews.getTotalElements(),
            runningCrews.getNumber(),
            runningCrews.getSize(),
            runningCrews.getContent().stream()
                .map(RunningCrewSummaryResponse::from)
                .toList()
        );
    }
}
