package com.dobugs.yologaapi.service.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;

public record RunningCrewsResponse(long totalElements, int page, int size, List<RunningCrewResponse> content) {

    public static RunningCrewsResponse from(final Page<RunningCrew> runningCrews) {
        return new RunningCrewsResponse(
            runningCrews.getTotalElements(),
            runningCrews.getNumber(),
            runningCrews.getSize(),
            runningCrews.getContent().stream()
                .map(RunningCrewResponse::from)
                .toList()
        );
    }
}
