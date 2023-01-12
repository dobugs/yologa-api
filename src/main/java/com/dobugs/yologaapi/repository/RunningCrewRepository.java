package com.dobugs.yologaapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;

public interface RunningCrewRepository extends JpaRepository<RunningCrew, Long> {
}
