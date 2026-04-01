package com.tablenow.tablenow.domain.auth.service;

import com.tablenow.tablenow.domain.auth.dto.request.LoginRequest;
import com.tablenow.tablenow.domain.auth.dto.request.ReissueRequest;
import com.tablenow.tablenow.domain.auth.dto.request.SignupRequest;
import com.tablenow.tablenow.domain.auth.dto.response.TokenResponse;

import java.util.UUID;

public interface AuthService
{
    TokenResponse login(LoginRequest loginRequest);
    TokenResponse reissue(ReissueRequest reissueRequest);
    void signup(SignupRequest signupRequest);
    void logout(UUID userId);
}
