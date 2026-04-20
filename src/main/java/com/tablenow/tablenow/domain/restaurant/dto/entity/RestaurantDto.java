package com.tablenow.tablenow.domain.restaurant.dto.entity;

import java.time.Instant;
import java.util.UUID;

public record RestaurantDto
(
        UUID id,
        String name,
        String description,
        String address,
        String addressDetail,
        Instant createdAt,
        Instant updatedAt
) {}
