package com.tablenow.tablenow.domain.auth.entity;


import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_tokens")
@Entity
public class RefreshToken extends BaseEntity
{
    @Column(name = "token", columnDefinition = "TEXT", nullable = false)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder
    public RefreshToken(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String update(String newRefreshToken) {
        this.token = newRefreshToken;
        return this.token;
    }
}

