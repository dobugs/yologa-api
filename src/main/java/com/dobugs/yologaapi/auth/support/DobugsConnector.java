package com.dobugs.yologaapi.auth.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DobugsConnector implements Connector {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private final String authHost;
    private final String authPath;

    public DobugsConnector(
        @Value("${auth.host}") final String authHost,
        @Value("${auth.path}") final String authPath
    ) {
        this.authHost = authHost;
        this.authPath = authPath;
    }

    public <T> ResponseEntity<T> post(final Object request, final Class<T> responseType) {
        return REST_TEMPLATE.postForEntity(authHost + authPath, request, responseType);
    }
}
