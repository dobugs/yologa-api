package com.dobugs.yologaapi.service.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.dobugs.yologaapi.repository.dto.response.ParticipantDto;

public record ParticipantsResponse(long totalElements, int page, int size, List<ParticipantResponse> content) {

    public static ParticipantsResponse from(final Page<ParticipantDto> participants) {
        return new ParticipantsResponse(
            participants.getTotalElements(),
            participants.getNumber(),
            participants.getSize(),
            participants.getContent().stream()
                .map(ParticipantResponse::from)
                .toList()
        );
    }
}
