package com.dobugs.yologaapi.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dobugs.yologaapi.test.dto.request.TestRunningCrewCreateRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
@RestController
public class TestController {

    private final TestService testService;

    @PostMapping("/participants")
    public ResponseEntity<Void> createParticipants(@RequestParam("runningCrewId") final Long runningCrewId) {
        testService.createParticipants(runningCrewId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/runningCrews")
    public ResponseEntity<Void> createRunningCrews(
        @RequestParam("count") final Integer count,
        @RequestBody final TestRunningCrewCreateRequest request
    ) {
        testService.createRunningCrews(count, request);
        return ResponseEntity.ok().build();
    }
}
