package com.tablenow.tablenow.domain.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablenow.tablenow.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler
{
    private final ObjectMapper objectMapper;

    /* 필터 레벨 권한 거부 */
    /* AccessDeniedException(authorizeHttpRequests, 403) */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "FORBIDDEN",
                "접근 권한이 없습니다.",
                Map.of(),
                "AccessDeniedException",
                403
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
