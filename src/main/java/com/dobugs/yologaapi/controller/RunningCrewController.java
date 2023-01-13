package com.dobugs.yologaapi.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dobugs.yologaapi.service.RunningCrewService;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/running-crews")
@RestController
public class RunningCrewController {

    private final RunningCrewService runningCrewService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final RunningCrewCreateRequest request) {
        final long runningCrewId = runningCrewService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/running-crews/" + runningCrewId)).build();
    }

    @GetMapping("/{runningCrewId}")
    public ResponseEntity<RunningCrewResponse> findById(@PathVariable final Long runningCrewId) {
        final RunningCrewResponse response = runningCrewService.findById(runningCrewId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{runningCrewId}")
    public ResponseEntity<Void> update(
        @PathVariable final Long runningCrewId,
        @RequestBody final RunningCrewUpdateRequest request
    ) {
        runningCrewService.update(runningCrewId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{runningCrewId}")
    public ResponseEntity<Void> delete(@PathVariable final Long runningCrewId) {
        runningCrewService.delete(runningCrewId);
        return ResponseEntity.ok().build();
    }
}
