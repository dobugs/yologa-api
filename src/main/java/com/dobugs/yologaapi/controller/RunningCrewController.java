package com.dobugs.yologaapi.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dobugs.yologaapi.auth.Authorized;
import com.dobugs.yologaapi.service.RunningCrewService;
import com.dobugs.yologaapi.service.dto.request.PagingRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewFindNearbyRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewStatusRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewFindNearbyResponse;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;
import com.dobugs.yologaapi.service.dto.response.RunningCrewsResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/running-crews")
@RestController
public class RunningCrewController {

    private final RunningCrewService runningCrewService;

    @Authorized
    @PostMapping
    public ResponseEntity<Void> create(
        @RequestHeader("Authorization") final String accessToken,
        @RequestBody final RunningCrewCreateRequest request
    ) {
        final long runningCrewId = runningCrewService.create(accessToken, request);
        return ResponseEntity.created(URI.create("/api/v1/running-crews/" + runningCrewId)).build();
    }

    @GetMapping
    public ResponseEntity<RunningCrewFindNearbyResponse> findNearby(@ModelAttribute final RunningCrewFindNearbyRequest request) {
        final RunningCrewFindNearbyResponse response = runningCrewService.findNearby(request);
        return ResponseEntity.ok().body(response);
    }

    @Authorized
    @GetMapping("/in-progress")
    public ResponseEntity<RunningCrewsResponse> findInProgress(
        @RequestHeader("Authorization") final String accessToken,
        @ModelAttribute final PagingRequest request
    ) {
        final RunningCrewsResponse response = runningCrewService.findInProgress(accessToken, request);
        return ResponseEntity.ok().body(response);
    }

    @Authorized
    @GetMapping("/hosted")
    public ResponseEntity<RunningCrewsResponse> findHosted(
        @RequestHeader("Authorization") final String accessToken,
        @ModelAttribute final RunningCrewStatusRequest request
    ) {
        final RunningCrewsResponse response = runningCrewService.findHosted(accessToken, request);
        return ResponseEntity.ok().body(response);
    }

    @Authorized
    @GetMapping("/participated")
    public ResponseEntity<RunningCrewsResponse> findParticipated(
        @RequestHeader("Authorization") final String accessToken,
        @ModelAttribute final RunningCrewStatusRequest request
    ) {
        final RunningCrewsResponse response = runningCrewService.findParticipated(accessToken, request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{runningCrewId}")
    public ResponseEntity<RunningCrewResponse> findById(@PathVariable final Long runningCrewId) {
        final RunningCrewResponse response = runningCrewService.findById(runningCrewId);
        return ResponseEntity.ok().body(response);
    }

    @Authorized
    @PutMapping("/{runningCrewId}")
    public ResponseEntity<Void> update(
        @RequestHeader("Authorization") final String accessToken,
        @PathVariable final Long runningCrewId,
        @RequestBody final RunningCrewUpdateRequest request
    ) {
        runningCrewService.update(accessToken, runningCrewId, request);
        return ResponseEntity.ok().build();
    }

    @Authorized
    @DeleteMapping("/{runningCrewId}")
    public ResponseEntity<Void> delete(
        @RequestHeader("Authorization") final String accessToken,
        @PathVariable final Long runningCrewId
    ) {
        runningCrewService.delete(accessToken, runningCrewId);
        return ResponseEntity.ok().build();
    }

    @Authorized
    @PostMapping("/{runningCrewId}/start")
    public ResponseEntity<Void> start(
        @RequestHeader("Authorization") final String accessToken,
        @PathVariable final Long runningCrewId
    ) {
        runningCrewService.start(accessToken, runningCrewId);
        return ResponseEntity.ok().build();
    }

    @Authorized
    @PostMapping("/{runningCrewId}/end")
    public ResponseEntity<Void> end(
        @RequestHeader("Authorization") final String accessToken,
        @PathVariable final Long runningCrewId
    ) {
        runningCrewService.end(accessToken, runningCrewId);
        return ResponseEntity.ok().build();
    }
}
