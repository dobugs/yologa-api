package com.dobugs.yologaapi.auth;

import java.lang.annotation.Annotation;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.dobugs.yologaapi.auth.dto.response.AuthorizationResponse;
import com.dobugs.yologaapi.auth.exception.AuthorizationConnectorException;
import com.dobugs.yologaapi.auth.support.Connector;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final Connector dobugsConnector;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        if (!hasMethodAnnotation((HandlerMethod) handler, Authorized.class)) {
            return true;
        }
        final ResponseEntity<AuthorizationResponse> authorizationResponse = dobugsConnector.post(request, AuthorizationResponse.class);
        validateConnectionResponseIsSuccess(authorizationResponse);
        return true;
    }

    private <A extends Annotation> boolean hasMethodAnnotation(final HandlerMethod handler, final Class<A> annotationType) {
        return handler.hasMethodAnnotation(annotationType);
    }

    private void validateConnectionResponseIsSuccess(final ResponseEntity<AuthorizationResponse> response) {
        final HttpStatusCode statusCode = response.getStatusCode();
        if (!statusCode.is2xxSuccessful()) {
            throw new AuthorizationConnectorException(getResponseMessage(response));
        }
    }

    private String getResponseMessage(final ResponseEntity<AuthorizationResponse> response) {
        final AuthorizationResponse body = response.getBody();
        if (body == null) {
            return "두벅스 서버와의 연결에 실패하였습니다.";
        }
        return body.message();
    }
}
