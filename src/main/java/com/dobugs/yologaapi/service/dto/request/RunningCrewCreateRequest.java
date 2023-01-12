package com.dobugs.yologaapi.service.dto.request;

import java.time.LocalDateTime;

import com.dobugs.yologaapi.service.dto.common.DateDto;
import com.dobugs.yologaapi.service.dto.common.LocationDto;

public record RunningCrewCreateRequest(String title, LocationDto location, int capacity, DateDto date,
                                       LocalDateTime deadline, String description) {

}
