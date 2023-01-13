package com.dobugs.yologaapi.service.dto.common;

public class CoordinatesDto {

    private Double latitude;
    private Double longitude;

    private CoordinatesDto() {
    }

    public CoordinatesDto(final Double latitude, final Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
