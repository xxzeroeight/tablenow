package com.tablenow.tablenow.domain.auth.service;

import com.tablenow.tablenow.domain.auth.dto.request.LoginRequest;
import com.tablenow.tablenow.domain.auth.dto.request.ReissueRequest;
import com.tablenow.tablenow.domain.auth.dto.request.SignupRequest;
import com.tablenow.tablenow.domain.auth.dto.response.TokenResponse;
import com.tablenow.tablenow.domain.auth.entity.RefreshToken;
import com.tablenow.tablenow.domain.auth.repository.RefreshTokenRepository;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.domain.user.repository.UserRepository;
import com.tablenow.tablenow.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .build());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    @Override
    public TokenResponse reissue(ReissueRequest reissueRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(reissueRequest.refreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        String userId = jwtProvider.getSubject(reissueRequest.refreshToken());
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        String newAccessToken = jwtProvider.createAccessToken(user);
        String newRefreshToken = jwtProvider.createRefreshToken(user);

        refreshToken.update(newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    @Override
    public void signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new IllegalArgumentException("Invalid email");
        }

        User user = User.builder()
                .name(signupRequest.name())
                .password(passwordEncoder.encode(signupRequest.password()))
                .email(signupRequest.email())
                .username(signupRequest.nickname())
                .phoneNumber(signupRequest.phoneNumber())
                .build();

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        refreshTokenRepository.deleteByUser(user);
    }
}
