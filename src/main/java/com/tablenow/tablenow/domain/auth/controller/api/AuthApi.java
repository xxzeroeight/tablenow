package com.tablenow.tablenow.domain.auth.controller.api;

import com.tablenow.tablenow.domain.auth.dto.request.LoginRequest;
import com.tablenow.tablenow.domain.auth.dto.request.SignupRequest;
import com.tablenow.tablenow.domain.auth.dto.response.TokenResponse;
import com.tablenow.tablenow.global.exception.ErrorResponse;
import com.tablenow.tablenow.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증/인가 API")
public interface AuthApi
{
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다. Refresh Token은 쿠키로 발급됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest);

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "이메일 중복",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest signupRequest);

    @Operation(summary = "토큰 재발급", description = "쿠키의 Refresh Token으로 Access Token을 재발급합니다. Refresh Token도 함께 갱신됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<TokenResponse> reissue(@CookieValue(name = "refresh_token") String refreshToken);

    @Operation(summary = "로그아웃", description = "리프레시 토큰을 삭제하고 로그아웃합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "미인증 사용자")
    })
    ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails);
}
