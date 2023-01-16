package com.dobugs.yologaapi.service.dto.common;

import org.locationtech.jts.geom.Point;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LocationDto {

    private CoordinatesDto departure;

    public LocationDto(final CoordinatesDto departure) {
        this.departure = departure;
    }

    public static LocationDto from(final Point departure) {
        return new LocationDto(CoordinatesDto.from(departure));
    }
}
