package com.dobugs.yologaapi.service.dto.request;

import java.time.LocalDateTime;

import com.dobugs.yologaapi.service.dto.common.DateDto;
import com.dobugs.yologaapi.service.dto.common.LocationsDto;

public record RunningCrewUpdateRequest(String title, LocationsDto location, int capacity, DateDto date,
                                       LocalDateTime deadline, String description) {

}
