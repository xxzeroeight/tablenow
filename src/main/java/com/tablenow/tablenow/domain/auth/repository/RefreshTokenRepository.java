package com.tablenow.tablenow.domain.auth.repository;

import com.tablenow.tablenow.domain.auth.entity.RefreshToken;
import com.tablenow.tablenow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID>
{
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
