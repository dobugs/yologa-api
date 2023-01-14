package com.dobugs.yologaapi.service.dto.request;

public record RunningCrewFindNearbyRequest(Double latitude, Double longitude, int radius, int page, int size) {
}
