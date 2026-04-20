package com.tablenow.tablenow.domain.restaurant.dto.request;

import java.util.UUID;

public record UpdateRestaurantRequest
(
        String name,
        String description,
        String address,
        String addressDetail,
        UUID categoryId
) {}
