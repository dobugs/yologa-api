package com.dobugs.yologaapi.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dobugs.yologaapi.exception.dto.response.ExceptionResponse;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequest(Exception e) {
        final ExceptionResponse response = ExceptionResponse.from(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
