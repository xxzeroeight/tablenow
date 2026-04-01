package com.tablenow.tablenow.domain.auth.dto.response;

public record TokenResponse
(
        String accessToken,
        String refreshToken
) {}
