package com.dobugs.yologaapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;

public interface RunningCrewRepository extends JpaRepository<RunningCrew, Long> {

    Optional<RunningCrew> findByIdAndArchived(final Long id, final boolean archived);

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
}
