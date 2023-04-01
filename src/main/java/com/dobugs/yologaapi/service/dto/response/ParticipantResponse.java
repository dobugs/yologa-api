package com.dobugs.yologaapi.service.dto.response;

import com.dobugs.yologaapi.repository.dto.response.ParticipantDto;

public record ParticipantResponse(Long id, String nickname) {

    public static ParticipantResponse from(final ParticipantDto participantDto) {
        return new ParticipantResponse(participantDto.getId(), participantDto.getNickname());
    }
}
