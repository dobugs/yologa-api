package com.dobugs.yologaapi.test;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dobugs.yologaapi.domain.runningcrew.Capacity;
import com.dobugs.yologaapi.domain.runningcrew.Coordinates;
import com.dobugs.yologaapi.domain.runningcrew.Deadline;
import com.dobugs.yologaapi.domain.runningcrew.Participant;
import com.dobugs.yologaapi.domain.runningcrew.ParticipantType;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.ParticipantRepository;
import com.dobugs.yologaapi.repository.RunningCrewRepository;
import com.dobugs.yologaapi.service.dto.common.CoordinatesDto;
import com.dobugs.yologaapi.test.dto.request.TestRunningCrewCreateRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class TestService {

    private static final int MAXIMUM_RUNNING_CREW_COUNT = 10_000;

    private final RunningCrewRepository runningCrewRepository;
    private final ParticipantRepository participantRepository;

    /**
     * runningCrewId 를 가지는 더미 참여자 정보를 저장한다.
     * - 참여자가 주최자밖에 없는 경우만 가능
     * - 더미 사용자의 모든 데이터를 사용하여 참여자로 저장
     */
    public void createParticipants(final Long runningCrewId) {
        if (runningCrewId == null) {
            throw new IllegalArgumentException("runningCrewId 를 입력해주세요");
        }

        final RunningCrew runningCrew = runningCrewRepository.findById(runningCrewId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 러닝크루입니다."));
        final int participantsSize = runningCrew.getParticipants().getValue().size();
        if (participantsSize > 2) {
            throw new IllegalArgumentException("참여자가 없는 러닝크루만 추가 가능합니다.");
        }

        final long first = 21; // 참여자 아이디
        final long last = 174;
        for (long memberId = first; memberId <= last; memberId++) {
            participantRepository.save(new Participant(memberId, ParticipantType.PARTICIPATING, runningCrew));
        }
    }

    /**
     * 입력한 count 수만큼 request 에 정의한 위치를 가지는 러닝크루를 저장한다.
     */
    public void createRunningCrews(final Integer count, final TestRunningCrewCreateRequest request) {
        if (count == null || request == null) {
            throw new IllegalArgumentException("count 와 location 을 입력해주세요");
        }
        if (count > MAXIMUM_RUNNING_CREW_COUNT) {
            throw new IllegalArgumentException(String.format("count 는 %d 개 이하만 가능합니다.", MAXIMUM_RUNNING_CREW_COUNT));
        }

        final LocalDateTime now = LocalDateTime.now();

        final long hostId = 20;
        final Capacity capacity = new Capacity(200);
        final LocalDateTime scheduledStartDate = now.plusYears(2);
        final LocalDateTime scheduledEndDate = scheduledStartDate.plusHours(1);
        final Deadline deadline = new Deadline(now.plusYears(1));

        for (int i = 0; i < count; i++) {
            final Coordinates departure = selectCoordinates(request.location().getDeparture());
            final Coordinates arrival = selectCoordinates(request.location().getArrival());
            runningCrewRepository.save(
                new RunningCrew(
                    hostId, departure, arrival, capacity,
                    scheduledStartDate, scheduledEndDate, deadline, "title", "description"
                )
            );
        }
    }

    private Coordinates selectCoordinates(final CoordinatesDto coordinates) {
        return new Coordinates(
            selectData(coordinates.getLatitude()),
            selectData(coordinates.getLongitude())
        );
    }

    private Double selectData(final Double data) {
        final double minimum = data - 0.05;
        final double random = Math.random() * 0.1;
        return minimum + random;
    }
}
