package com.dobugs.yologaapi.auth.dto.response;

public record ServiceToken(Long memberId, String provider, String tokenType, String token) {
}
