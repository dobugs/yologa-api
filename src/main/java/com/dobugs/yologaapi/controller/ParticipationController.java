package com.dobugs.yologaapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dobugs.yologaapi.service.ParticipationService;
import com.dobugs.yologaapi.service.dto.request.ParticipantsRequest;
import com.dobugs.yologaapi.service.dto.response.ParticipantsResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/running-crews/{runningCrewId}")
@RestController
public class ParticipationController {

    private final ParticipationService participationService;

    @GetMapping("/participants")
    public ResponseEntity<ParticipantsResponse> findParticipants(
        @PathVariable final Long runningCrewId,
        @ModelAttribute final ParticipantsRequest request
    ) {
        final ParticipantsResponse response = participationService.findParticipants(runningCrewId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/participate")
    public ResponseEntity<Void> participate(
        @RequestHeader("Authorization") final String accessToken,
        @PathVariable final Long runningCrewId
    ) {
        participationService.participate(accessToken, runningCrewId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancel(
        @RequestHeader("Authorization") final String accessToken,
        @PathVariable final Long runningCrewId
    ) {
        participationService.cancel(accessToken, runningCrewId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
        @RequestHeader("Authorization") final String accessToken,
        @PathVariable final Long runningCrewId
    ) {
        participationService.withdraw(accessToken, runningCrewId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{memberId}")
    public ResponseEntity<Void> accept(
        @RequestHeader("Authorization") final String accessToken,
        @PathVariable final Long runningCrewId,
        @PathVariable final Long memberId
    ) {
        participationService.accept(accessToken, runningCrewId, memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{memberId}")
    public ResponseEntity<Void> reject(
        @RequestHeader("Authorization") final String accessToken,
        @PathVariable final Long runningCrewId,
        @PathVariable final Long memberId
    ) {
        participationService.reject(accessToken, runningCrewId, memberId);
        return ResponseEntity.ok().build();
    }
}
