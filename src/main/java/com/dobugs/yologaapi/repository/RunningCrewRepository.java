package com.dobugs.yologaapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.dobugs.yologaapi.domain.runningcrew.ProgressionType;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;

import jakarta.persistence.LockModeType;

public interface RunningCrewRepository extends JpaRepository<RunningCrew, Long> {

    Optional<RunningCrew> findByIdAndArchivedIsTrue(final Long id);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RunningCrew r WHERE r.id = :id AND r.archived = true")
    Optional<RunningCrew> findByIdAndArchivedIsTrueForUpdate(final Long id);

    @Query(
        value = "SELECT *\n"
            + "FROM running_crew\n"
            + "WHERE ST_Distance_Sphere(POINT(?2, ?1), departure) <= ?3 AND archived = true\n"
            + ";",
        countQuery = "SELECT count(id)\n"
            + "FROM running_crew\n"
            + "WHERE ST_Distance_Sphere(POINT(?2, ?1), departure) <= ?3 AND archived = true\n"
            + ";",
        nativeQuery = true
    )
    Page<RunningCrew> findNearby(final Double latitude, final Double longitude, final int radius, final Pageable pageable);

    @Query(
        value = "SELECT rc.*\n"
            + "FROM running_crew AS rc\n"
            + "JOIN participant AS p\n"
            + "ON rc.id = p.running_crew_id\n"
            + "WHERE \n"
            + "rc.status = ?2 AND rc.archived = true\n"
            + "AND\n"
            + "p.member_id = ?1 AND p.status = ?3 AND p.archived = true\n"
            + ";",
        countQuery = "SELECT count(rc.id)\n"
            + "FROM running_crew AS rc\n"
            + "JOIN participant AS p\n"
            + "ON rc.id = p.running_crew_id\n"
            + "WHERE \n"
            + "rc.status = ?2 AND rc.archived = true\n"
            + "AND\n"
            + "p.member_id = ?1 AND p.status = ?3 AND p.archived = true\n"
            + ";",
        nativeQuery = true
    )
    Page<RunningCrew> findInProgress(final Long memberId, final String runningCrewStatus, final String participantStatus, final Pageable pageable);

    Page<RunningCrew> findByMemberIdAndArchivedIsTrue(final Long memberId, final Pageable pageable);

    Page<RunningCrew> findByMemberIdAndStatusAndArchivedIsTrue(final Long memberId, final ProgressionType progressionType, final Pageable pageable);

    @Query(
        value = "SELECT rc.*\n"
            + "FROM running_crew AS rc\n"
            + "JOIN participant AS p\n"
            + "ON rc.id = p.running_crew_id\n"
            + "WHERE\n"
            + "p.member_id = ?1 AND p.status = ?2 AND p.archived = true\n"
            + "AND\n"
            + "rc.archived = true\n"
            + ";",
        countQuery = "SELECT count(rc.id)\n"
            + "FROM running_crew AS rc\n"
            + "JOIN participant AS p\n"
            + "ON rc.id = p.running_crew_id\n"
            + "WHERE\n"
            + "p.member_id = ?1 AND p.status = ?2 AND p.archived = true\n"
            + "AND\n"
            + "rc.archived = true\n"
            + ";",
        nativeQuery = true
    )
    Page<RunningCrew> findParticipated(final Long memberId, final String participantStatus, final Pageable pageable);

    @Query(
        value = "SELECT rc.*\n"
            + "FROM running_crew AS rc\n"
            + "JOIN participant AS p\n"
            + "ON rc.id = p.running_crew_id\n"
            + "WHERE\n"
            + "p.member_id = ?1 AND p.status = ?3 AND p.archived = true\n"
            + "AND\n"
            + "rc.status = ?2 AND rc.archived = true\n"
            + ";",
        countQuery = "SELECT count(rc.id)\n"
            + "FROM running_crew AS rc\n"
            + "JOIN participant AS p\n"
            + "ON rc.id = p.running_crew_id\n"
            + "WHERE\n"
            + "p.member_id = ?1 AND p.status = ?3 AND p.archived = true\n"
            + "AND\n"
            + "rc.status = ?2 AND rc.archived = true\n"
            + ";",
        nativeQuery = true
    )
    Page<RunningCrew> findParticipatedByStatus(final Long memberId, final String runningCrewStatus, final String participantStatus, final Pageable pageable);
}
