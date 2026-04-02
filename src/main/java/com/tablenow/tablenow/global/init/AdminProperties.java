package com.tablenow.tablenow.global.init;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "admin")
public record AdminProperties
(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String username,
        @NotBlank String phoneNumber
) {}
