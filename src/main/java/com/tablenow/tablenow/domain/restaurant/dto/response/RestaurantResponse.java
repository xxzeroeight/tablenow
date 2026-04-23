package com.tablenow.tablenow.domain.restaurant.dto.response;

import com.tablenow.tablenow.domain.restaurant.dto.entity.RestaurantDto;

import java.time.Instant;
import java.util.UUID;

public record RestaurantResponse
(
        UUID id,
        String name,
        String description,
        String address,
        String addressDetail,
        Instant createdAt,
        Instant updatedAt
) {
    public static RestaurantResponse from(RestaurantDto restaurantDto) {
        return new RestaurantResponse(
                restaurantDto.id(),
                restaurantDto.name(),
                restaurantDto.description(),
                restaurantDto.address(),
                restaurantDto.addressDetail(),
                restaurantDto.createdAt(),
                restaurantDto.updatedAt()
        );
    }
}
