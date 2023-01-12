package com.dobugs.yologaapi.domain.runningcrew;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Geometry;

import com.dobugs.yologaapi.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RunningCrew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long memberId;

    @Column(nullable = false, columnDefinition = "Geometry")
    private Geometry departure;

    @Column(nullable = false, columnDefinition = "Geometry")
    private Geometry arrival;

    @Enumerated(value = EnumType.STRING)
    private RunningCrewProgression status;

    @Embedded
    private Capacity capacity;

    @Column(nullable = false)
    private LocalDateTime scheduledStartDate;

    @Column(nullable = false)
    private LocalDateTime scheduledEndDate;

    @Column
    private LocalDateTime implementedStartDate;

    @Column
    private LocalDateTime implementedEndDate;

    @Embedded
    private Deadline deadline;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    protected RunningCrew() {
    }
}
