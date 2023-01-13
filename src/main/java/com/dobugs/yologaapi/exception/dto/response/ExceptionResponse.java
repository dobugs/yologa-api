package com.dobugs.yologaapi.exception.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ExceptionResponse {

    private final String message;

    public static ExceptionResponse from(final String message) {
        return new ExceptionResponse(message);
    }
}
