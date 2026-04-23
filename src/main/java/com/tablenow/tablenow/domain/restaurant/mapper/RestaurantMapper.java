package com.tablenow.tablenow.domain.restaurant.mapper;

import com.tablenow.tablenow.domain.restaurant.dto.entity.RestaurantDto;
import com.tablenow.tablenow.domain.restaurant.entity.Restaurant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestaurantMapper
{
    RestaurantDto toDto(Restaurant restaurant);
}
