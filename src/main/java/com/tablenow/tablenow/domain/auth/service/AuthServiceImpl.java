package com.tablenow.tablenow.domain.auth.service;

import com.tablenow.tablenow.domain.auth.dto.request.LoginRequest;
import com.tablenow.tablenow.domain.auth.dto.request.ReissueRequest;
import com.tablenow.tablenow.domain.auth.dto.request.SignupRequest;
import com.tablenow.tablenow.domain.auth.dto.response.TokenDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 이메일, 비밀번호로 인증 후 Access/Refresh Token을 발급.
     * <p>
     * 기존 Refresh Token은 삭제 후 새로 저장 (DB에는 해시값으로 저장)
     *
     * @param loginRequest 이메일, 비밀번호
     * @return 발급된 {@link TokenResponse}
     */
    @Transactional
    @Override
    public TokenDto login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UserNotFoundException(loginRequest.email()));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);

        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        existingToken -> existingToken.update(hashToken(refreshToken)),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                        .token(hashToken(refreshToken))
                                        .user(user)
                                        .build())
                );

        return new TokenDto(accessToken, refreshToken);
    }

    /**
     * Refresh Token을 검증하고 Access/Refresh Token을 재발급.
     * <p>
     * Refresh Token은 갱신 (rotate).
     *
     * @param reissueRequest 재발급에 사용할 Refresh Token
     * @return 재발급된 {@link TokenResponse}
     */
    @Transactional
    @Override
    public TokenResponse reissue(ReissueRequest reissueRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashToken(reissueRequest.refreshToken()))
                .orElseThrow(InvalidRefreshTokenException::new);

        String userId = jwtProvider.getSubject(reissueRequest.refreshToken());
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException(UUID.fromString(userId)));

        String newAccessToken = jwtProvider.createAccessToken(user);
        String newRefreshToken = jwtProvider.createRefreshToken(user);

        refreshToken.update(hashToken(newRefreshToken));

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    /**
     * 신규 사용자를 등록.
     * <p>
     * 이메일 중복 시 예외 발생. 비밀번호는 BCrypt로 암호화하여 저장.
     *
     * @param signupRequest 이름, 이메일, 비밀번호, 닉네임, 전화번호
     */
    @Transactional
    @Override
    public void signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new DuplicateEmailException(signupRequest.email());
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

    /**
     * 해당 사용자의 Refresh Token을 삭제해 로그아웃 처리.
     *
     * @param userId 로그아웃할 사용자 ID
     */
    @Transactional
    @Override
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        refreshTokenRepository.deleteByUser(user);
    }

    /**
     * 토큰을 SHA-256으로 해싱해 반환.
     *
     * @param token 해싱할 토큰 문자열
     * @return 16진수 문자열로 인코딩된 해시값
     */
    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
