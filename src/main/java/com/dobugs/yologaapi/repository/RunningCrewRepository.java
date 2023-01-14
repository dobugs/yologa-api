package com.dobugs.yologaapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;

public interface RunningCrewRepository extends JpaRepository<RunningCrew, Long> {

    Optional<RunningCrew> findByIdAndArchived(final Long id, final boolean archived);
}
