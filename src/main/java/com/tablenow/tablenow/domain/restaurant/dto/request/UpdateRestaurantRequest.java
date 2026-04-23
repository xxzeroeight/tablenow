package com.tablenow.tablenow.domain.restaurant.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateRestaurantRequest
(
        @Size(max = 50)
        String name,

        @Size(max = 255)
        String description,

        @Size(max = 100)
        String address,

        @Size(max = 100)
        String addressDetail,

        @NotNull
        UUID categoryId
) {}
