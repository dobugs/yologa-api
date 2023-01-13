package com.dobugs.yologaapi.service.dto.common;

public class LocationsDto {

    private CoordinatesDto departure;
    private CoordinatesDto arrival;

    private LocationsDto() {
    }

    public LocationsDto(final CoordinatesDto departure, final CoordinatesDto arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }

    public CoordinatesDto getDeparture() {
        return departure;
    }

    public CoordinatesDto getArrival() {
        return arrival;
    }
}
