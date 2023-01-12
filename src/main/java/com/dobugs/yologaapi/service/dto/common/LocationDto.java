package com.dobugs.yologaapi.service.dto.common;

public class LocationDto {

    private CoordinatesDto departure;
    private CoordinatesDto arrival;

    private LocationDto() {
    }

    public LocationDto(final CoordinatesDto departure, final CoordinatesDto arrival) {
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
