package com.tablenow.tablenow.domain.auth.dto.response;

public record TokenDto
(
        String accessToken,
        String refreshToken
) {}
