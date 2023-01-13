package com.dobugs.yologaapi.domain.runningcrew;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

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
    private Point departure;

    @Column(nullable = false, columnDefinition = "Geometry")
    private Point arrival;

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

    public RunningCrew(final Long memberId, final Coordinates departure, final Coordinates arrival,
        final Capacity capacity, final LocalDateTime scheduledStartDate, final LocalDateTime scheduledEndDate,
        final Deadline deadline, final String title, final String description
    ) {
        this.memberId = memberId;
        this.departure = wktToPoint(departure);
        this.arrival = wktToPoint(arrival);
        this.status = RunningCrewProgression.CREATED;
        this.capacity = capacity;
        this.scheduledStartDate = scheduledStartDate;
        this.scheduledEndDate = scheduledEndDate;
        this.deadline = deadline;
        this.title = title;
        this.description = description;
    }

    private Point  wktToPoint(final Coordinates coordinates) {
        final String wellKnownText = String.format("POINT(%f %f)", coordinates.latitude(), coordinates.longitude());
        try {
            return (Point) new WKTReader().read(wellKnownText);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Long getId() {
        return id;
    }
}
