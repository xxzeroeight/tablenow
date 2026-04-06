package com.tablenow.tablenow.domain.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablenow.tablenow.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint
{
    private final ObjectMapper objectMapper;

    /* 필터 레벨 인증 실패 */
    /* AuthenticationException(token 없음/만료/변조, 401) */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "UNAUTHORIZED",
                "인증이 필요합니다.",
                Map.of(),
                "AuthenticationException",
                401
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
