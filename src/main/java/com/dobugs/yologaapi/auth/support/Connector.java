package com.dobugs.yologaapi.auth.support;

import org.springframework.http.ResponseEntity;

public interface Connector {

    <T> ResponseEntity<T> post(Object request, Class<T> responseType);
}
