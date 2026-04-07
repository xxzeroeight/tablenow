package com.tablenow.tablenow.domain.auth.controller;

import com.tablenow.tablenow.domain.auth.controller.api.AuthApi;
import com.tablenow.tablenow.domain.auth.dto.request.LoginRequest;
import com.tablenow.tablenow.domain.auth.dto.request.ReissueRequest;
import com.tablenow.tablenow.domain.auth.dto.request.SignupRequest;
import com.tablenow.tablenow.domain.auth.dto.response.TokenDto;
import com.tablenow.tablenow.domain.auth.dto.response.TokenResponse;
import com.tablenow.tablenow.domain.auth.service.AuthService;
import com.tablenow.tablenow.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthApi
{
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest)
    {
        TokenDto tokenDto = authService.login(loginRequest);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokenDto.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new TokenResponse(tokenDto.accessToken()));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest signupRequest)
    {
        authService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestBody @Valid ReissueRequest reissueRequest)
    {
        TokenResponse tokenResponse = authService.reissue(reissueRequest);

        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        authService.logout(customUserDetails.getUserId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
