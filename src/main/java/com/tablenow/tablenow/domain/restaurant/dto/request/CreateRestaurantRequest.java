package com.tablenow.tablenow.domain.restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateRestaurantRequest
(
        @NotBlank
        @Size(max = 50)
        String name,

        @NotBlank
        @Size(max = 255)
        String description,

        @NotBlank
        @Size(max = 100)
        String address,

        @NotBlank
        @Size(max = 100)
        String addressDetail,

        @NotNull
        UUID categoryId
) {}
