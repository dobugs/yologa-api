package com.dobugs.yologaapi.service.dto.common;

import org.locationtech.jts.geom.Point;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LocationsDto {

    private CoordinatesDto departure;
    private CoordinatesDto arrival;

    public LocationsDto(final CoordinatesDto departure, final CoordinatesDto arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }

    public static LocationsDto from(final Point departure, final Point arrival) {
        return new LocationsDto(
            CoordinatesDto.from(departure),
            CoordinatesDto.from(arrival)
        );
    }
}
