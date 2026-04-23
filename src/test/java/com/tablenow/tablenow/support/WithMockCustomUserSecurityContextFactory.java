package com.tablenow.tablenow.support;

import com.tablenow.tablenow.domain.user.entity.Role;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.domain.user.repository.UserRepository;
import com.tablenow.tablenow.global.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser>
{
    @Autowired
    private UserRepository userRepository;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        String id = UUID.randomUUID().toString().substring(0, 8);

        User saved = userRepository.saveAndFlush(User.builder()
                .name(annotation.name())
                .email(id + "@test.com")
                .username("user_" + id)
                .password("encoded")
                .phoneNumber("010" + Math.abs(id.hashCode()) % 100000000)
                .role(Role.valueOf(annotation.role()))
                .build());

        CustomUserDetails userDetails = new CustomUserDetails(saved);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
