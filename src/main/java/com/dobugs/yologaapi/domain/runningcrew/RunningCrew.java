package com.dobugs.yologaapi.domain.runningcrew;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import com.dobugs.yologaapi.domain.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
    private ProgressionType status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "running_crew_id")
    private List<Participant> participant = new ArrayList<>();

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

    public RunningCrew(final Long memberId, final Coordinates departure, final Coordinates arrival,
        final Capacity capacity, final LocalDateTime scheduledStartDate, final LocalDateTime scheduledEndDate,
        final Deadline deadline, final String title, final String description
    ) {
        validateStartIsBeforeThanEnd(scheduledStartDate, scheduledEndDate);
        this.memberId = memberId;
        this.departure = wktToPoint(departure);
        this.arrival = wktToPoint(arrival);
        this.status = ProgressionType.CREATED;
        this.participant.add(new Participant(memberId));
        this.capacity = capacity;
        this.scheduledStartDate = scheduledStartDate;
        this.scheduledEndDate = scheduledEndDate;
        this.deadline = deadline;
        this.title = title;
        this.description = description;
    }

    public void update(
        final Long memberId,
        final Coordinates departure, final Coordinates arrival,
        final Capacity capacity, final LocalDateTime scheduledStartDate, final LocalDateTime scheduledEndDate,
        final Deadline deadline, final String title, final String description
    ) {
        validateMemberIsHost(memberId);
        validateStartIsBeforeThanEnd(scheduledStartDate, scheduledEndDate);
        this.departure = wktToPoint(departure);
        this.arrival = wktToPoint(arrival);
        this.capacity = capacity;
        this.scheduledStartDate = scheduledStartDate;
        this.scheduledEndDate = scheduledEndDate;
        this.deadline = deadline;
        this.title = title;
        this.description = description;
    }

    public void delete(final Long memberId) {
        validateMemberIsHost(memberId);
        deleteEntity();
    }

    public void start(final Long memberId) {
        validateMemberIsHost(memberId);
        validateRunningCrewDoesNotStart();
        status = ProgressionType.IN_PROGRESS;
        implementedStartDate = LocalDateTime.now();
    }

    public void end(final Long memberId) {
        validateMemberIsHost(memberId);
        validateRunningCrewStart();
        validateRunningCrewDoesNotEnd();
        status = ProgressionType.COMPLETED;
        implementedEndDate = LocalDateTime.now();
    }

    private void validateMemberIsHost(final Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new IllegalArgumentException(String.format("호스트가 아닙니다. [%s]", memberId));
        }
    }

    private void validateStartIsBeforeThanEnd(final LocalDateTime start, final LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                String.format("시작 시간은 종료 시간보다 앞서있어야 합니다. [start : %s / end : %s]", start, end)
            );
        }
    }

    private void validateRunningCrewDoesNotStart() {
        if (implementedStartDate != null) {
            throw new IllegalArgumentException(String.format("이미 시작되었습니다. [%s]", implementedStartDate));
        }
    }

    private void validateRunningCrewStart() {
        if (implementedStartDate == null) {
            throw new IllegalArgumentException("아직 시작하지 않았습니다.");
        }
    }

    private void validateRunningCrewDoesNotEnd() {
        if (implementedEndDate != null) {
            throw new IllegalArgumentException(String.format("이미 종료되었습니다. [%s]", implementedEndDate));
        }
    }

    private Point  wktToPoint(final Coordinates coordinates) {
        final String wellKnownText = String.format("POINT(%f %f)", coordinates.longitude(), coordinates.latitude());
        try {
            return (Point) new WKTReader().read(wellKnownText);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
