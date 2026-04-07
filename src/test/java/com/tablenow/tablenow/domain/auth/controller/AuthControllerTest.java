package com.tablenow.tablenow.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablenow.tablenow.domain.auth.dto.request.LoginRequest;
import com.tablenow.tablenow.domain.auth.dto.request.SignupRequest;
import com.tablenow.tablenow.domain.auth.dto.response.TokenDto;
import com.tablenow.tablenow.domain.auth.service.AuthService;
import com.tablenow.tablenow.global.security.CustomUserDetailsService;
import com.tablenow.tablenow.global.security.JwtProvider;
import com.tablenow.tablenow.global.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(CookieUtil.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController Slice Test")
class AuthControllerTest
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AuthService authService;
    @MockitoBean private JwtProvider jwtProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {
        @Test
        @DisplayName("로그인 시 유효한 요청이면 200과 토큰을 반환한다.")
        void givenValidRequest_whenLogin_thenReturn200WithToken() throws Exception{
            // given
            LoginRequest loginRequest = new LoginRequest("test@naver.com", "TEst1234!!");
            TokenDto tokenDto = new TokenDto("access-token", "refresh-token");

            given(authService.login(any(LoginRequest.class))).willReturn(tokenDto);

            // when & then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("access-token"))
                    .andExpect(jsonPath("$.refreshToken").doesNotExist())
                    .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token=refresh-token")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Secure")))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Strict")));
        }

        @Test
        @DisplayName("로그인 시 이메일이 비어있으면 400을 반환한다.")
        void givenBlankEmail_whenLogin_thenReturn400() throws Exception {
            // given
            LoginRequest loginRequest = new LoginRequest("", "TEst1234!!");

            // when & then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("로그인 시 이메일 형식이 잘못되면 400을 반환한다.")
        void givenInvalidEmailFormat_whenLogin_thenReturn400() throws Exception {
            // given
            LoginRequest loginRequest = new LoginRequest("email", "TEst1234!!");

            // when & then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/signup")
    class SignUp {
        @Test
        @DisplayName("회원가입 시 유효한 요청이면 201을 반환한다.")
        void givenValidRequest_whenSignup_thenReturn201() throws Exception {
            // given
            SignupRequest signupRequest = new SignupRequest(
                    "tester", "테스트", "test@naver.com", "TEst1234!!", "01012345678"
            );

            // when & then
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("회원가입 시 비밀번호 형식이 잘못되면 400을 반환한다.")
        void givenInvalidPassword_whenSignup_thenReturn400() throws Exception {
            // given
            SignupRequest signupRequest = new SignupRequest(
                    "tester", "테스트", "test@naver.com", "!", "01012345678"
            );

            // when & then
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("회원가입 시 전화번호 형식이 잘못되면 400을 반환한다.")
        void givenInvalidPhoneNumber_whenSignup_thenReturn400() throws Exception {
            // given
            SignupRequest signupRequest = new SignupRequest(
                    "tester", "테스트", "test@naver.com", "TEst1234!!", "0101234"
            );

            // when & then
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/reissue")
    class Reissue {
        @Test
        @DisplayName("재발급 시 유효한 요청이면 200과 새 토큰을 반환한다.")
        void givenValidRequest_whenReissue_thenReturn200WithToken() throws Exception {
            // given
            TokenDto tokenDto = new TokenDto("new-access-token", "new-refresh-token");

            given(authService.reissue("old-refresh-token")).willReturn(tokenDto);

            // when & then
            mockMvc.perform(post("/api/auth/reissue")
                            .cookie(new Cookie("refresh_token", "old-refresh-token")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                    .andExpect(jsonPath("$.refreshToken").doesNotExist())
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token=new-refresh-token")));
        }

        @Test
        @DisplayName("재발급 시 쿠키가 없으면 401을 반환한다.")
        void givenMissingCookie_whenReissue_thenReturn401() throws Exception {
            // when & then
            mockMvc.perform(post("/api/auth/reissue"))
                    .andExpect(status().isUnauthorized());
        }
    }
}