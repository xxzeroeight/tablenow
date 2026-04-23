package com.tablenow.tablenow.domain.restaurant.service;

import com.tablenow.tablenow.domain.category.entity.Category;
import com.tablenow.tablenow.domain.category.exception.CategoryNotFoundException;
import com.tablenow.tablenow.domain.category.repository.CategoryRepository;
import com.tablenow.tablenow.domain.restaurant.dto.entity.RestaurantDto;
import com.tablenow.tablenow.domain.restaurant.dto.request.CreateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.dto.request.UpdateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.entity.Restaurant;
import com.tablenow.tablenow.domain.restaurant.exception.RestaurantAccessDeniedException;
import com.tablenow.tablenow.domain.restaurant.exception.RestaurantNotFoundException;
import com.tablenow.tablenow.domain.restaurant.mapper.RestaurantMapper;
import com.tablenow.tablenow.domain.restaurant.repository.RestaurantRepository;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.domain.user.exception.UserNotFoundException;
import com.tablenow.tablenow.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RestaurantServiceImpl implements RestaurantService
{
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    @Override
    public RestaurantDto createRestaurant(UUID userId, CreateRestaurantRequest createRestaurantRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Category category = categoryRepository.findById(createRestaurantRequest.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(createRestaurantRequest.categoryId()));

        Restaurant restaurant = Restaurant.builder()
                .name(createRestaurantRequest.name())
                .description(createRestaurantRequest.description())
                .address(createRestaurantRequest.address())
                .addressDetail(createRestaurantRequest.addressDetail())
                .user(user)
                .category(category)
                .build();

        restaurantRepository.save(restaurant);
        return restaurantMapper.toDto(restaurant);
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    @Override
    public RestaurantDto updateRestaurant(UUID userId, UUID restaurantId, UpdateRestaurantRequest updateRestaurantRequest) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        validateOwner(userId, restaurantId, restaurant);

        Category category = categoryRepository.findById(updateRestaurantRequest.categoryId())
                        .orElseThrow(() -> new CategoryNotFoundException(updateRestaurantRequest.categoryId()));

        restaurant.update(
                updateRestaurantRequest.name(),
                updateRestaurantRequest.description(),
                updateRestaurantRequest.address(),
                updateRestaurantRequest.addressDetail(),
                category
        );

        return restaurantMapper.toDto(restaurant);
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    @Override
    public void deleteRestaurant(UUID userId, UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        validateOwner(userId, restaurantId, restaurant);

        restaurantRepository.delete(restaurant);
    }

    private void validateOwner(UUID userId, UUID restaurantId, Restaurant restaurant) {
        User owner = restaurant.getUser();

        if (owner == null || !Objects.equals(userId, owner.getId())) {
            throw new RestaurantAccessDeniedException(restaurantId);
        }
    }
}
