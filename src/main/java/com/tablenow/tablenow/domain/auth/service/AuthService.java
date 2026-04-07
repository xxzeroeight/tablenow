package com.tablenow.tablenow.domain.auth.service;

import com.tablenow.tablenow.domain.auth.dto.request.LoginRequest;
import com.tablenow.tablenow.domain.auth.dto.request.SignupRequest;
import com.tablenow.tablenow.domain.auth.dto.response.TokenDto;

import java.util.UUID;

public interface AuthService
{
    TokenDto login(LoginRequest loginRequest);
    TokenDto reissue(String refreshToken);
    void signup(SignupRequest signupRequest);
    void logout(UUID userId);
}
