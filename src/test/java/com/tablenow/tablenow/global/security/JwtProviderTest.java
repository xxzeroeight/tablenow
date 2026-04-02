package com.tablenow.tablenow.global.security;

import com.tablenow.tablenow.domain.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtProvider Unit Test")
class JwtProviderTest
{
    private JwtProvider jwtProvider;
    private User user;

    private static final String SECRET = "test-secret-key-that-is-at-least-32-bytes-long!";
    private static final long ACCESS_EXPIRATION = 1000 * 60 * 15;       // 15분
    private static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24; // 1일

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SECRET, ACCESS_EXPIRATION, REFRESH_EXPIRATION);

        user = User.builder()
                .name("테스트")
                .email("test@naver.com")
                .username("tester")
                .password("TEst1234!!")
                .phoneNumber("01012345678")
                .build();

        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
    }

    @Nested
    @DisplayName("액세스 토큰")
    class AccessToken {
        @Test
        @DisplayName("액세스 토큰 생성 시 subject가 userId와 일치한다.")
        void givenValidUser_whenCreateAccessToken_thenSubjectMatchesUserId() {
            String token = jwtProvider.createAccessToken(user);

            assertThat(jwtProvider.getSubject(token)).isEqualTo(user.getId().toString());
        }

        @Test
        @DisplayName("액세스 토큰 생성 시 토큰 타입이 access이다.")
        void givenValidUser_whenCreateAccessToken_thenTokenTypeIsAccess() {
            String token = jwtProvider.createAccessToken(user);

            assertThat(jwtProvider.getTokenType(token)).isEqualTo("access");
        }
    }

    @Nested
    @DisplayName("리프레시 토큰")
    class RefreshToken {
        @Test
        @DisplayName("리프레시 토큰 생성 시 subject가 userId와 일치한다.")
        void givenValidUser_whenCreateRefreshToken_thenSubjectMatchesUserId() {
            String token = jwtProvider.createRefreshToken(user);

            assertThat(jwtProvider.getSubject(token)).isEqualTo(user.getId().toString());
        }

        @Test
        @DisplayName("리프레시 토큰 생성 시 토큰 타입이 refresh이다.")
        void givenValidUser_whenCreateRefreshToken_thenTokenTypeIsRefresh() {
            String token = jwtProvider.createRefreshToken(user);

            assertThat(jwtProvider.getTokenType(token)).isEqualTo("refresh");
        }
    }

    @Nested
    @DisplayName("토큰 검증 실패")
    class TokenValidationFailure {
        @Test
        @DisplayName("subject 조회 시 만료된 토큰이면 ExpiredJwtException을 던진다.")
        void givenExpiredToken_whenGetSubject_thenThrowExpiredJwtException() {
            JwtProvider expiredProvider = new JwtProvider(SECRET, -1000, -1000);
            String token = expiredProvider.createAccessToken(user);

            assertThatThrownBy(() -> jwtProvider.getSubject(token))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        @DisplayName("subject 조회 시 변조된 토큰이면 예외를 던진다.")
        void givenTamperedToken_whenGetSubject_thenThrowException() {
            String token = jwtProvider.createAccessToken(user);
            String tampered = token.substring(0, token.length() - 5) + "xxxxx";

            assertThatThrownBy(() -> jwtProvider.getSubject(tampered))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("subject 조회 시 잘못된 형식이면 MalformedJwtException을 던진다.")
        void givenMalformedToken_whenGetSubject_thenThrowMalformedJwtException() {
            assertThatThrownBy(() -> jwtProvider.getSubject("not-a-jwt"))
                    .isInstanceOf(MalformedJwtException.class);
        }

        @Test
        @DisplayName("subject 조회 시 다른 시크릿이면 SignatureException을 던진다.")
        void givenDifferentSecret_whenGetSubject_thenThrowSignatureException() {
            JwtProvider otherProvider = new JwtProvider(
                    "different-secret-key-that-is-at-least-32-bytes!", ACCESS_EXPIRATION, REFRESH_EXPIRATION);
            String token = otherProvider.createAccessToken(user);

            assertThatThrownBy(() -> jwtProvider.getSubject(token))
                    .isInstanceOf(SignatureException.class);
        }
    }
}