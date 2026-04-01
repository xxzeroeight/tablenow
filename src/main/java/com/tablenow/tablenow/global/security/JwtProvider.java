package com.tablenow.tablenow.global.security;

import com.tablenow.tablenow.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider
{
    private final String secret;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.access-expiration}") long accessExpiration,
                       @Value("${jwt.refresh-expiration}") long refreshExpiration)
    {
        this.secret = secret;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String createAccessToken(User user) {
        return createToken(user, accessExpiration, "access");
    }

    public String createRefreshToken(User user) {
        return createToken(user, refreshExpiration, "refresh");
    }

    public String getTokenType(String token) {
        return parseToken(token).get("type", String.class);
    }

    public String getSubject(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * JWT를 생성.
     * <p>
     * subject는 userId(UUID), claim에 토큰 타입("access", "refresh")을 포함.
     *
     * @param user          토큰 발급 대상 사용자
     * @param expiration    만료 시간(밀리초)
     * @param type          토큰 타입("access", "refresh")
     * @return              서명된 JWT 문자열
     */
    private String createToken(User user, long expiration, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * JWT를 파싱해 {@link Claims}를 반환.
     *
     * @param token JWT 문자열
     * @return      파싱된 {@link Claims}
     * @throws ExpiredJwtException      토큰이 만료된 경우
     * @throws MalformedJwtException    토큰 형식이 잘못된 경우
     * @throws SecurityException        서명 검증 실패 시
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * HMAC-SHA 알고리즘으로 서명 키를 생성.
     *
     * @return {@link SecretKey}
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
