package com.dobugs.yologaapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dobugs.yologaapi.domain.runningcrew.Participant;
import com.dobugs.yologaapi.repository.dto.response.ParticipantDto;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query(
        value = "SELECT m.id AS id, m.nickname AS nickname\n"
            + "FROM participant AS p\n"
            + "LEFT JOIN member AS m\n"
            + "ON p.member_id = m.id\n"
            + "WHERE p.running_crew_id = ?1 AND p.status = ?2 AND p.archived = true\n"
            + ";",
        countQuery = "SELECT (p.id)\n"
            + "FROM participant AS p\n"
            + "LEFT JOIN member AS m\n"
            + "ON p.member_id = m.id\n"
            + "WHERE p.running_crew_id = ?1 AND p.status = ?2 AND p.archived = true\n"
            + ";",
        nativeQuery = true
    )
    List<ParticipantDto> findParticipants(final Long runningCrewId, final String participantType);
}
