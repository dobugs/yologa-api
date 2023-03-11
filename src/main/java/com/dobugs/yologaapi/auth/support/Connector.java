package com.dobugs.yologaapi.auth.support;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface Connector {

    <T> ResponseEntity<T> post(HttpServletRequest request, Class<T> responseType);
}
