package com.dobugs.yologaapi.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dobugs.yologaapi.support.dto.response.UserTokenResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

class TokenGeneratorTest {

    private static final String SECRET_KEY_VALUE = "secretKey".repeat(10);
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_VALUE.getBytes(StandardCharsets.UTF_8));

    private TokenGenerator tokenGenerator;

    @BeforeEach
    void setUp() {
        tokenGenerator = new TokenGenerator(SECRET_KEY_VALUE);
    }

    @DisplayName("토큰 추출 테스트")
    @Nested
    public class extract {

        private static final long MEMBER_ID = 0L;
        private static final String PROVIDER = "google";
        private static final String ACCESS_TOKEN = "accessToken";
        private static final Date EXPIRATION = new Date(new Date().getTime() + 10_000);

        @DisplayName("token 을 추출한다")
        @Test
        void success() {
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN, EXPIRATION);

            final UserTokenResponse userTokenResponse = tokenGenerator.extract(serviceToken);

            assertAll(
                () -> assertThat(userTokenResponse.memberId()).isEqualTo(MEMBER_ID),
                () -> assertThat(userTokenResponse.token()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(userTokenResponse.provider()).isEqualTo(PROVIDER)
            );
        }

        @DisplayName("잘못된 형식의 JWT 일 경우 예외가 발생한다")
        @Test
        void JWTIsMalformed() {
            final String serviceToken = "malformedToken";

            assertThatThrownBy(() -> tokenGenerator.extract(serviceToken))
                .isInstanceOf(MalformedJwtException.class);
        }

        @DisplayName("지원하지 않는 JWT 일 경우 예외가 발생한다")
        @Test
        void JWTIsUnsupported() {
            final String serviceToken = Jwts.builder()
                .claim("memberId", MEMBER_ID)
                .claim("provider", PROVIDER)
                .claim("token", ACCESS_TOKEN)
                .setExpiration(EXPIRATION)
                .compact();

            assertThatThrownBy(() -> tokenGenerator.extract(serviceToken))
                .isInstanceOf(UnsupportedJwtException.class);
        }

        @DisplayName("서명이 다른 JWT 일 경우 예외가 발생한다")
        @Test
        void signatureIsDifferent() {
            final SecretKey differentSecretKey = Keys.hmacShaKeyFor("differentKey".repeat(10).getBytes(StandardCharsets.UTF_8));
            final String serviceToken = Jwts.builder()
                .claim("memberId", MEMBER_ID)
                .claim("provider", PROVIDER)
                .claim("token", ACCESS_TOKEN)
                .setExpiration(EXPIRATION)
                .signWith(differentSecretKey, SignatureAlgorithm.HS256)
                .compact();

            assertThatThrownBy(() -> tokenGenerator.extract(serviceToken))
                .isInstanceOf(SignatureException.class);
        }

        @DisplayName("만료 시간이 지난 JWT 일 경우 예외가 발생한다")
        @Test
        void JWTIsExpired() {
            final Date expiration = new Date(new Date().getTime() - 1);
            final String serviceToken = createToken(MEMBER_ID, PROVIDER, ACCESS_TOKEN, expiration);

            assertThatThrownBy(() -> tokenGenerator.extract(serviceToken))
                .isInstanceOf(ExpiredJwtException.class);
        }

        private String createToken(final Long memberId, final String provider, final String token, final Date expiration) {
            return Jwts.builder()
                .claim("memberId", memberId)
                .claim("provider", provider)
                .claim("token", token)
                .setExpiration(expiration)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
        }
    }
}
