package com.dobugs.yologaapi.support.dto.response;

public record UserTokenResponse(Long memberId, String provider, String token) {
}
