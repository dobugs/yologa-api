package com.dobugs.yologaapi.service.dto.request;

public record RunningCrewFindNearbyRequest(Double latitude, Double longitude, int radius, Integer page, Integer size) {
}
