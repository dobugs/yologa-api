package com.dobugs.yologaapi.auth.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class DobugsConnector implements Connector {

    private final RestTemplate REST_TEMPLATE = new RestTemplate();

    private final String authHost;
    private final String authPath;

    public DobugsConnector(
        @Value("${auth.host}") final String authHost,
        @Value("${auth.path}") final String authPath
    ) {
        this.authHost = authHost;
        this.authPath = authPath;
        config();
    }

    public <T> ResponseEntity<T> post(final HttpServletRequest request, final Class<T> responseType) {
        final String authorization = request.getHeader("Authorization");
        return REST_TEMPLATE.postForEntity(
            authHost + authPath,
            createRequestEntity(authorization),
            responseType
        );
    }

    private HttpEntity<MultiValueMap<String, String>> createRequestEntity(final String token) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", token);
        return new HttpEntity<>(headers);
    }

    private void config() {
        REST_TEMPLATE.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        REST_TEMPLATE.setErrorHandler(new DefaultResponseErrorHandler() {
            public boolean hasError(final ClientHttpResponse response) {
                return false;
            }
        });
    }
}
