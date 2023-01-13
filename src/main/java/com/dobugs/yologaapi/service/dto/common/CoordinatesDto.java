package com.dobugs.yologaapi.service.dto.common;

import org.locationtech.jts.geom.Point;

public class CoordinatesDto {

    private Double latitude;
    private Double longitude;

    private CoordinatesDto() {
    }

    public CoordinatesDto(final Double latitude, final Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static CoordinatesDto from(final Point point) {
        return new CoordinatesDto(
            point.getX(),
            point.getY()
        );
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
