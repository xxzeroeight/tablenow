package com.tablenow.tablenow.global.init;

import com.tablenow.tablenow.domain.user.entity.Role;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminAccountInitializer implements CommandLineRunner
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @Transactional
    @Override
    public void run(String... args) {
        // admin이 없는 경우에만 생성
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        try {
            User admin = User.builder()
                    .name("관리자")
                    .email(adminProperties.email())
                    .password(passwordEncoder.encode(adminProperties.password()))
                    .username(adminProperties.username())
                    .phoneNumber(adminProperties.phoneNumber())
                    .build();

            admin.changeRole(Role.ADMIN);
            userRepository.save(admin);
        } catch (DataIntegrityViolationException e) {
            log.info("[ADMIN INIT] 이미 생성된 관리자 계정이 존재합니다.");
        }
    }
}
