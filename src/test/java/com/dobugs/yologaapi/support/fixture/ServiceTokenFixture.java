package com.dobugs.yologaapi.support.fixture;

import java.util.Date;

import javax.crypto.SecretKey;

import com.dobugs.yologaapi.auth.dto.response.ServiceToken;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class ServiceTokenFixture {

    public static class Builder {

        private Long memberId;
        private String provider;
        private String tokenType;
        private String token;
        private Date expiration;
        private SecretKey secretKey;
        private SignatureAlgorithm algorithm;

        public Builder() {
        }

        public String compact() {
            final JwtBuilder jwtBuilder = Jwts.builder();
            if (memberId != null) {
                jwtBuilder.claim("memberId", memberId);
            }
            if (provider != null) {
                jwtBuilder.claim("provider", provider);
            }
            if (tokenType != null) {
                jwtBuilder.claim("tokenType", tokenType);
            }
            if (token != null) {
                jwtBuilder.claim("token", token);
            }
            if (expiration != null) {
                jwtBuilder.setExpiration(expiration);
            }
            if (secretKey != null && algorithm != null) {
                jwtBuilder.signWith(secretKey, algorithm);
            }
            return jwtBuilder.compact();
        }

        public ServiceToken build() {
            return new ServiceToken(memberId, provider, tokenType, token);
        }

        public Builder memberId(final Long memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder provider(final String provider) {
            this.provider = provider;
            return this;
        }

        public Builder tokenType(final String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder token(final String token) {
            this.token = token;
            return this;
        }

        public Builder expiration(final Date expiration) {
            this.expiration = expiration;
            return this;
        }

        public Builder secretKey(final SecretKey secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder algorithm(final SignatureAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }
    }
}
