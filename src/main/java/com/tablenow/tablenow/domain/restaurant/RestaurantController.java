package com.tablenow.tablenow.domain.restaurant;

import com.tablenow.tablenow.domain.restaurant.dto.entity.RestaurantDto;
import com.tablenow.tablenow.domain.restaurant.dto.request.CreateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.dto.request.UpdateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.dto.response.RestaurantResponse;
import com.tablenow.tablenow.domain.restaurant.service.RestaurantService;
import com.tablenow.tablenow.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
@RestController
public class RestaurantController
{
    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> createdRestaurant(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                @RequestBody CreateRestaurantRequest createRestaurantRequest)
    {
        UUID userId = customUserDetails.getUserId();

        RestaurantDto restaurantDto = restaurantService.createRestaurant(userId, createRestaurantRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestaurantResponse.from(restaurantDto));
    }

    @PatchMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> updatedRestaurant(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                @PathVariable UUID restaurantId,
                                                                @RequestBody UpdateRestaurantRequest updateRestaurantRequest)
    {
        UUID userId = customUserDetails.getUserId();

        RestaurantDto restaurantDto = restaurantService.updateRestaurant(userId, restaurantId, updateRestaurantRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RestaurantResponse.from(restaurantDto));
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                 @PathVariable UUID restaurantId)
    {
        UUID userId = customUserDetails.getUserId();

        restaurantService.deleteRestaurant(userId, restaurantId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
