package com.tablenow.tablenow.domain.restaurant.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class RestaurantNotFoundException extends RestaurantException
{
    public RestaurantNotFoundException(UUID restaurantId) {
        super(ErrorCode.RESTAURANT_NOT_FOUND, Map.of("restaurantId", restaurantId));
    }
}
