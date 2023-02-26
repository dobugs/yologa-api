package com.dobugs.yologaapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dobugs.yologaapi.service.ParticipationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/running-crews/{runningCrewId}")
@RestController
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping("/participate")
    public ResponseEntity<Void> participate(
        @RequestHeader("Authorization") final String accessToken,
        @PathVariable final Long runningCrewId
    ) {
        participationService.participate(accessToken, runningCrewId);
        return ResponseEntity.ok().build();
    }
}
