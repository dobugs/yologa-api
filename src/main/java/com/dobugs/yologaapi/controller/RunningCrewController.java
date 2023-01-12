package com.dobugs.yologaapi.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dobugs.yologaapi.service.RunningCrewService;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;

@RequestMapping("/api/v1/running-crews")
@RestController
public class RunningCrewController {

    private final RunningCrewService runningCrewService;

    public RunningCrewController(final RunningCrewService runningCrewService) {
        this.runningCrewService = runningCrewService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final RunningCrewCreateRequest request) {
        final long runningCrewId = runningCrewService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/running-crews/" + runningCrewId)).build();
    }
}
