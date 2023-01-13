package com.dobugs.yologaapi.service.dto.common;

import org.locationtech.jts.geom.Point;

public class LocationsDto {

    private CoordinatesDto departure;
    private CoordinatesDto arrival;

    private LocationsDto() {
    }

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

    public CoordinatesDto getDeparture() {
        return departure;
    }

    public CoordinatesDto getArrival() {
        return arrival;
    }
}
