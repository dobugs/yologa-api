package com.dobugs.yologaapi.service.dto.common;

import org.locationtech.jts.geom.Point;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CoordinatesDto {

    private Double latitude;
    private Double longitude;

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
}
