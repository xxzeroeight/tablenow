package com.tablenow.tablenow.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablenow.tablenow.domain.auth.dto.request.LoginRequest;
import com.tablenow.tablenow.domain.auth.dto.request.SignupRequest;
import com.tablenow.tablenow.domain.auth.dto.response.TokenResponse;
import com.tablenow.tablenow.domain.auth.repository.RefreshTokenRepository;
import com.tablenow.tablenow.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("Auth Integration Test")
public class AuthIntegrationTest
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;

    private final SignupRequest signupRequest = new SignupRequest(
            "tester", "테스트", "test@naver.com", "TEst1234!!", "01012345678");

    private final LoginRequest loginRequest = new LoginRequest("test@naver.com", "TEst1234!!");

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 → 로그인 → 재발급 → 로그아웃 전체 플로우가 성공한다.")
    void givenNewUser_whenFullAuthFlow_thenSuccess() throws Exception {
        // 1. 회원가입
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated());

        assertThat(userRepository.findByEmail("test@naver.com")).isPresent();

        // 2. 로그인
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().httpOnly("refresh_token", true))
                .andReturn();

        TokenResponse loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                TokenResponse.class
        );
        Cookie loginCookie = loginResult.getResponse().getCookie("refresh_token");

        assertThat(loginCookie).isNotNull();

        // 3. 재발급
        MvcResult reissueResult = mockMvc.perform(post("/api/auth/reissue")
                        .cookie(loginCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

        TokenResponse reissueResponse = objectMapper.readValue(
                reissueResult.getResponse().getContentAsString(),
                TokenResponse.class
        );
        Cookie reissueCookie = reissueResult.getResponse().getCookie("refresh_token");

        assertThat(reissueResponse.accessToken()).isNotEmpty();
        assertThat(reissueCookie.getValue()).isEqualTo(loginCookie.getValue());

        // 4. 로그아웃
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + reissueResponse.accessToken()))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("refresh_token", 0));

        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }
    
    @Test
    @DisplayName("로그인 시 잘못된 비밀번호면 401을 반환한다.")
    void givenInvalidPassword_whenLogin_thenReturn401() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated());

        LoginRequest wrongPassword = new LoginRequest("test@naver.com", "wrong password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPassword)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("재발급 시 잘못된 리프레시토큰이면 401을 반환한다.")
    void givenInvalidRefreshToken_whenReissue_thenReturn401() throws Exception {
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("valid-refresh-token")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그아웃 시 미인증 사용자면 401을 반환한다.")
    void givenUnauthenticated_whenLogout_thenReturn401() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }
}
