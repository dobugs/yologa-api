package com.dobugs.yologaapi.repository;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class H2Function {

    public static Point POINT(final Double latitude, final Double longitude) {
        return wktToPoint(latitude, longitude);
    }

    public static double ST_Distance_Sphere(final Point start, final Point end) {
        return start.distance(end);
    }

    private static Point wktToPoint(final Double latitude, final Double longitude) {
        final String wellKnownText = String.format("POINT(%f %f)", longitude, latitude);
        try {
            return (Point) new WKTReader().read(wellKnownText);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
