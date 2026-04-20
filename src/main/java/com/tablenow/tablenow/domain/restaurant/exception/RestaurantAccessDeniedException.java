package com.tablenow.tablenow.domain.restaurant.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class RestaurantAccessDeniedException extends RestaurantException
{
    public RestaurantAccessDeniedException(UUID restaurantId) {
        super(ErrorCode.RESTAURANT_ACCESS_DENIED, Map.of("restaurantId", restaurantId));
    }
}
