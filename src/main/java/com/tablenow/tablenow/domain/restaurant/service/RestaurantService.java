package com.tablenow.tablenow.domain.restaurant.service;

import com.tablenow.tablenow.domain.restaurant.dto.entity.RestaurantDto;
import com.tablenow.tablenow.domain.restaurant.dto.request.CreateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.dto.request.UpdateRestaurantRequest;

import java.util.UUID;

public interface RestaurantService
{
    // create
    RestaurantDto createRestaurant(UUID userId, CreateRestaurantRequest createRestaurantRequest);

    // update
    RestaurantDto updateRestaurant(UUID userId, UUID restaurantId, UpdateRestaurantRequest updateRestaurantRequest);

    // delete
    void deleteRestaurant(UUID userId, UUID restaurantId);
}
