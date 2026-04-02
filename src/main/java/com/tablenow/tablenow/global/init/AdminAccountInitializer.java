package com.tablenow.tablenow.global.init;

import com.tablenow.tablenow.domain.user.entity.Role;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminAccountInitializer implements CommandLineRunner
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String adminEmail;
    private final String adminPassword;
    private final String adminUsername;

    public AdminAccountInitializer(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   @Value("${admin.email}") String adminEmail,
                                   @Value("${admin.password}") String adminPassword,
                                   @Value("${admin.username}") String adminUsername)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminUsername = adminUsername;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        // admin이 없는 경우에만 생성
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        User admin = User.builder()
                .name("관리자")
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .username(adminUsername)
                .phoneNumber("00000000000")
                .build();

        admin.changeRole(Role.ADMIN);
        userRepository.save(admin);
    }
}
