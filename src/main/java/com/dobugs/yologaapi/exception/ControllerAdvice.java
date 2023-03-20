package com.dobugs.yologaapi.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dobugs.yologaapi.auth.exception.AuthorizationException;
import com.dobugs.yologaapi.exception.dto.response.ExceptionResponse;
import com.dobugs.yologaapi.support.logging.UnhandledException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class ControllerAdvice {

    private static final int UNAUTHORIZED = 401;

    @UnhandledException
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleInternalServerError(Exception e) {
        final ExceptionResponse response = ExceptionResponse.from(e.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequest(Exception e) {
        final ExceptionResponse response = ExceptionResponse.from(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedJWT(MalformedJwtException e) {
        final ExceptionResponse response = ExceptionResponse.from("잘못된 형식의 JWT 입니다.");
        return ResponseEntity.status(UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ExceptionResponse> handleUnsupportedJWT(UnsupportedJwtException e) {
        final String message = "지원하지 않는 JWT 입니다.";
        final ExceptionResponse response = ExceptionResponse.from(message, e.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ExceptionResponse> handleSignatureException(SignatureException e) {
        final ExceptionResponse response = ExceptionResponse.from("두벅스의 JWT 가 아닙니다.");
        return ResponseEntity.status(UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException(ExpiredJwtException e) {
        final String message = "토큰의 만료 시간이 지났습니다.";
        final ExceptionResponse response = ExceptionResponse.from(message, e.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthorizationException(AuthorizationException e) {
        final ExceptionResponse response = ExceptionResponse.from(e.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(response);
    }
}
