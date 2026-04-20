package com.tablenow.tablenow.domain.restaurant.dto.request;

import java.util.UUID;

public record CreateRestaurantRequest
(
        String name,
        String description,
        String address,
        String addressDetail,
        UUID categoryId
) {}
