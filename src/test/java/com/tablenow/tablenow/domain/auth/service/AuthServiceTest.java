package com.tablenow.tablenow.domain.auth.service;

import com.tablenow.tablenow.domain.auth.dto.request.LoginRequest;
import com.tablenow.tablenow.domain.auth.dto.request.ReissueRequest;
import com.tablenow.tablenow.domain.auth.dto.request.SignupRequest;
import com.tablenow.tablenow.domain.auth.dto.response.TokenResponse;
import com.tablenow.tablenow.domain.auth.entity.RefreshToken;
import com.tablenow.tablenow.domain.auth.exception.DuplicateEmailException;
import com.tablenow.tablenow.domain.auth.exception.InvalidCredentialsException;
import com.tablenow.tablenow.domain.auth.exception.InvalidRefreshTokenException;
import com.tablenow.tablenow.domain.auth.repository.RefreshTokenRepository;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.domain.user.exception.UserNotFoundException;
import com.tablenow.tablenow.domain.user.repository.UserRepository;
import com.tablenow.tablenow.global.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("AuthService Unit Test")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest
{
    @InjectMocks private AuthServiceImpl authService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtProvider jwtProvider;
    @Mock private RefreshTokenRepository refreshTokenRepository;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = User.builder()
                .name("테스트")
                .email("test@naver.com")
                .username("tester")
                .password("TEst1234!!")
                .phoneNumber("01012345678")
                .build();

        ReflectionTestUtils.setField(user, "id", userId);
    }

    @Nested
    @DisplayName("로그인")
    class Login {
        @Test
        @DisplayName("유효한 자격증명이면 토큰을 반환한다.")
        void givenValidCredentials_whenLogin_thenReturnToken() {
            // given
            LoginRequest loginRequest = new LoginRequest("test@naver.com", "TEst1234!!");

            given(userRepository.findByEmail(loginRequest.email())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(loginRequest.password(), user.getPassword())).willReturn(true);
            given(jwtProvider.createAccessToken(user)).willReturn("access-token");
            given(jwtProvider.createRefreshToken(user)).willReturn("refresh-token");

            // when
            TokenResponse tokenResponse = authService.login(loginRequest);

            // then
            assertThat(tokenResponse.accessToken()).isEqualTo("access-token");
            assertThat(tokenResponse.refreshToken()).isEqualTo("refresh-token");

            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }
        
        @Test
        @DisplayName("존재하지 않는 이메일이면 예외를 던진다.")
        void givenInvalidEmail_whenLogin_thenThrowException() {
            // given
            LoginRequest loginRequest = new LoginRequest("test1234@naver.com", "TEst1234!!");

            given(userRepository.findByEmail(loginRequest.email())).willReturn(Optional.empty());
            
            // when
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(UserNotFoundException.class);
            
            // then
            
        }
        
        @Test
        @DisplayName("잘못된 비밀번호면 예외를 던진다.")
        void givenInvalidPassword_whenLogin_thenThrowException() {
            LoginRequest loginRequest = new LoginRequest("test@naver.com", "test1234!!");

            given(userRepository.findByEmail(loginRequest.email())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(loginRequest.password(), user.getPassword())).willReturn(false);

            // when
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(InvalidCredentialsException.class);
            
            // then

        }
    }
    
    @Nested
    @DisplayName("회원가입")
    class SignUp {
        @Test
        @DisplayName("유효한 요청이면 유저를 저장한다.")
        void givenValidRequest_whenSignup_thenSaveUser() {
            // given
            SignupRequest signupRequest = new SignupRequest(
                    "tester", "테스트", "test@naver.com", "TEst1234!!", "01012345678"
            );

            given(userRepository.existsByEmail(signupRequest.email())).willReturn(false);
            
            // when
            authService.signup(signupRequest);
            
            // then
            verify(userRepository).save(any(User.class));
        }
        
        @Test
        @DisplayName("중복 이메일이면 예외를 던진다.")
        void givenDuplicateEmail_whenSignup_thenThrowException() {
            // given
            SignupRequest signupRequest = new SignupRequest(
                    "tester", "테스트", "test@naver.com", "TEst1234!!", "01012345678"
            );

            given(userRepository.existsByEmail(signupRequest.email())).willReturn(true);
            
            // when
            assertThatThrownBy(() -> authService.signup(signupRequest))
                    .isInstanceOf(DuplicateEmailException.class);
            
            // then

        }
    }

    @Nested
    @DisplayName("리프레시 토큰 재발급")
    class Reissue {
        @Test
        @DisplayName("유효한 리프레시토큰이면 새 토큰을 반환한다.")
        void givenValidRefreshToken_whenReissue_thenReturnNewToken() {
            // given
            ReissueRequest reissueRequest = new ReissueRequest("old-refresh-token");
            RefreshToken refreshToken = RefreshToken.builder()
                    .token("hashed-token")
                    .user(user)
                    .build();

            given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.of(refreshToken));
            given(jwtProvider.getSubject(reissueRequest.refreshToken())).willReturn(userId.toString());
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(jwtProvider.createAccessToken(user)).willReturn("new-access-token");
            given(jwtProvider.createRefreshToken(user)).willReturn("new-refresh-token");

            // when
            TokenResponse tokenResponse = authService.reissue(reissueRequest);

            // then
            assertThat(tokenResponse.accessToken()).isEqualTo("new-access-token");
            assertThat(tokenResponse.refreshToken()).isEqualTo("new-refresh-token");
        }

        @Test
        @DisplayName("잘못된 리프레시토큰이면 예외를 던진다.")
        void givenInvalidRefreshToken_whenReissue_thenThrowException() {
            // given
            ReissueRequest reissueRequest = new ReissueRequest("invalid-token");

            given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> authService.reissue(reissueRequest))
                    .isInstanceOf(InvalidRefreshTokenException.class);

            // then

        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {
        @Test
        @DisplayName("유효한 유저면 리프레시토큰을 삭제한다.")
        void givenValidUser_whenLogout_thenDeleteRefreshToken() {
            // given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            authService.logout(userId);

            // then
            verify(refreshTokenRepository).deleteByUser(user);
        }

        @Test
        @DisplayName("존재하지 않는 유저면 예외를 던진다.")
        void givenInvalidUser_whenLogout_thenThrowException() {
            // given
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> authService.logout(userId))
                    .isInstanceOf(UserNotFoundException.class);

            // then

        }
    }
}