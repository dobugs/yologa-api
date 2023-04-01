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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dobugs.yologaapi.auth.Authorized;
import com.dobugs.yologaapi.auth.ExtractAuthorization;
import com.dobugs.yologaapi.auth.dto.response.ServiceToken;
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
        @ExtractAuthorization final ServiceToken serviceToken,
        @RequestBody final RunningCrewCreateRequest request
    ) {
        final long runningCrewId = runningCrewService.create(serviceToken, request);
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
        @ExtractAuthorization final ServiceToken serviceToken,
        @ModelAttribute final PagingRequest request
    ) {
        final RunningCrewsResponse response = runningCrewService.findInProgress(serviceToken, request);
        return ResponseEntity.ok().body(response);
    }

    @Authorized
    @GetMapping("/hosted")
    public ResponseEntity<RunningCrewsResponse> findHosted(
        @ExtractAuthorization final ServiceToken serviceToken,
        @ModelAttribute final RunningCrewStatusRequest request
    ) {
        final RunningCrewsResponse response = runningCrewService.findHosted(serviceToken, request);
        return ResponseEntity.ok().body(response);
    }

    @Authorized
    @GetMapping("/participated")
    public ResponseEntity<RunningCrewsResponse> findParticipated(
        @ExtractAuthorization final ServiceToken serviceToken,
        @ModelAttribute final RunningCrewStatusRequest request
    ) {
        final RunningCrewsResponse response = runningCrewService.findParticipated(serviceToken, request);
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
        @ExtractAuthorization final ServiceToken serviceToken,
        @PathVariable final Long runningCrewId,
        @RequestBody final RunningCrewUpdateRequest request
    ) {
        runningCrewService.update(serviceToken, runningCrewId, request);
        return ResponseEntity.ok().build();
    }

    @Authorized
    @DeleteMapping("/{runningCrewId}")
    public ResponseEntity<Void> delete(
        @ExtractAuthorization final ServiceToken serviceToken,
        @PathVariable final Long runningCrewId
    ) {
        runningCrewService.delete(serviceToken, runningCrewId);
        return ResponseEntity.ok().build();
    }

    @Authorized
    @PostMapping("/{runningCrewId}/start")
    public ResponseEntity<Void> start(
        @ExtractAuthorization final ServiceToken serviceToken,
        @PathVariable final Long runningCrewId
    ) {
        runningCrewService.start(serviceToken, runningCrewId);
        return ResponseEntity.ok().build();
    }

    @Authorized
    @PostMapping("/{runningCrewId}/end")
    public ResponseEntity<Void> end(
        @ExtractAuthorization final ServiceToken serviceToken,
        @PathVariable final Long runningCrewId
    ) {
        runningCrewService.end(serviceToken, runningCrewId);
        return ResponseEntity.ok().build();
    }
}
