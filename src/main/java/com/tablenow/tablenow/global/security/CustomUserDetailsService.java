package com.tablenow.tablenow.global.security;

import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;

    /**
     * userId(UUID)로 사용자를 조회해 {@link UserDetails}를 반환.
     * <p>
     * 사용자가 존재하지 않으면 {@link UsernameNotFoundException}을 던짐.
     *
     * @param userId UUID 형식의 사용자 식별자
     * @return {@link CustomUserDetails}
     * @throws UsernameNotFoundException 해당 userId의 사용자가 없을 때
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException(userId));

        return new CustomUserDetails(user);
    }
}
