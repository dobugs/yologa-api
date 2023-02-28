package com.dobugs.yologaapi.support;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dobugs.yologaapi.support.dto.response.UserTokenResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenGenerator {

    private static final String PAYLOAD_NAME_OF_MEMBER_ID = "memberId";
    private static final String PAYLOAD_NAME_OF_PROVIDER = "provider";
    private static final String PAYLOAD_NAME_OF_TOKEN = "token";

    private final SecretKey secretKey;

    public TokenGenerator(
        @Value("${jwt.token.secret-key}") final String secretKey
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));;
    }

    public UserTokenResponse extract(final String serviceToken) {
        final String jwt = serviceToken.replace("Bearer ", "");
        final Claims claims = extractClaims(jwt);
        final Long memberId = extractMemberId(claims);
        final String token = extractToken(claims);
        final String provider = extractProvider(claims);
        return new UserTokenResponse(memberId, provider, token);
    }

    private Long extractMemberId(final Claims claims) {
        final Integer memberId = (Integer)claims.get(PAYLOAD_NAME_OF_MEMBER_ID);
        return memberId.longValue();
    }

    private String extractProvider(final Claims claims) {
        return (String) claims.get(PAYLOAD_NAME_OF_PROVIDER);
    }

    private String extractToken(final Claims claims) {
        return (String) claims.get(PAYLOAD_NAME_OF_TOKEN);
    }

    private Claims extractClaims(final String serviceToken) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(serviceToken)
            .getBody();
    }
}
