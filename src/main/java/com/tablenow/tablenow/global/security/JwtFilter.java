package com.tablenow.tablenow.global.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter
{
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    /**
     * JWT Access Token을 검증하고 {@link SecurityContext}에 인증 정보를 설정.
     * <p>
     * 토큰이 없으면 인증 없이 다음 필터로 진행하고, 토큰 타입이 "access"가 아니거나
     * 만료/위변조된 경우 {@link SecurityContext}를 초기화.
     *
     * @param request           HTTP 요청
     * @param response          HHTP 응답
     * @param filterChain       필터 체인
     * @throws ServletException 서블릿 처리 중 오류 발생 시
     * @throws IOException      I/O 오류 발생 시
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = extractToken(request);

        if (accessToken != null) {
            try {
                String tokenType = jwtProvider.getTokenType(accessToken);

                if (!"access".equals(tokenType)) {
                    throw new IllegalArgumentException("invalid access token");
                }

                String userId = jwtProvider.getSubject(accessToken);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 헤더에서 Access Token을 추출.
     *
     * @param request HTTP 요청
     * @return 파싱된 토큰
     */
    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION_HEADER);

        if (bearer != null && bearer.startsWith(TOKEN_PREFIX)) {
            return bearer.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
