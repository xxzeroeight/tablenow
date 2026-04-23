package com.tablenow.tablenow.domain.restaurant.service;

import com.tablenow.tablenow.domain.category.entity.Category;
import com.tablenow.tablenow.domain.category.repository.CategoryRepository;
import com.tablenow.tablenow.domain.restaurant.dto.entity.RestaurantDto;
import com.tablenow.tablenow.domain.restaurant.dto.request.CreateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.dto.request.UpdateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.entity.Restaurant;
import com.tablenow.tablenow.domain.restaurant.exception.RestaurantAccessDeniedException;
import com.tablenow.tablenow.domain.restaurant.exception.RestaurantNotFoundException;
import com.tablenow.tablenow.domain.restaurant.mapper.RestaurantMapper;
import com.tablenow.tablenow.domain.restaurant.repository.RestaurantRepository;
import com.tablenow.tablenow.domain.user.entity.Role;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.domain.user.exception.UserNotFoundException;
import com.tablenow.tablenow.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("Restaurant Service Unit Test")
@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest
{
    @InjectMocks private RestaurantServiceImpl restaurantService;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private RestaurantMapper restaurantMapper;
    @Mock private UserRepository userRepository;

    private User user;
    private Category category;
    private Restaurant restaurant;

    private UUID userId;
    private UUID restaurantId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        user = User.builder()
                .name("사장님")
                .email("owner@test.com")
                .username("owner")
                .password("encoded")
                .phoneNumber("01012345678")
                .password("password")
                .role(Role.OWNER)
                .build();

        category = Category.builder()
                .name("한식")
                .build();

        restaurant = Restaurant.builder()
                .name("맛집")
                .description("설명")
                .address("서울시 강남구")
                .addressDetail("1층")
                .user(user)
                .category(category)
                .build();

        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(restaurant, "id", restaurantId);
        ReflectionTestUtils.setField(category, "id", categoryId);
    }

    @Nested
    @DisplayName("레스토랑 생성")
    class Create {
        @Test
        @DisplayName("정상적으로 레스토랑을 생성한다.")
        void givenValidRequest_whenCreate_thenSuccess() {
            // given
            CreateRestaurantRequest createRestaurantRequest = new CreateRestaurantRequest(
                    "맛집", "설명", "서울시 강남구", "1층", categoryId
            );

            RestaurantDto restaurantDto = new RestaurantDto(
                    restaurantId, "맛집", "설명", "서울시 강남구", "1층", Instant.now(), Instant.now()
            );

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
            given(restaurantRepository.save(any(Restaurant.class))).willReturn(restaurant);
            given(restaurantMapper.toDto(any(Restaurant.class))).willReturn(restaurantDto);

            // when
            RestaurantDto result = restaurantService.createRestaurant(userId, createRestaurantRequest);

            // then
            assertThat(result.name()).isEqualTo("맛집");
            assertThat(result.id()).isEqualTo(restaurantId);

            then(restaurantRepository).should().save(any(Restaurant.class));
            then(restaurantMapper).should().toDto(any(Restaurant.class));
        }

        @Test
        @DisplayName("존재하지 않는 유저로 생성 시 예외가 발생한다.")
        void givenInvalidUser_whenCreate_thenThrowsException() {
            // given
            CreateRestaurantRequest createRestaurantRequest = new CreateRestaurantRequest(
                    "맛집", "설명", "서울시 강남구", "1층", categoryId
            );

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> restaurantService.createRestaurant(userId, createRestaurantRequest))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("레스토랑 수정")
    class Update {
        @Test
        @DisplayName("정상적으로 레스토랑을 수정한다.")
        void givenValidRequest_whenUpdate_thenSuccess() {
            // given
            UpdateRestaurantRequest updateRestaurantRequest = new UpdateRestaurantRequest(
                    "새 맛집", "수정된 설명", "서울시 강남구", "1층", categoryId
            );

            RestaurantDto restaurantDto = new RestaurantDto(
                    restaurantId, "새 맛집", "수정된 설명", "서울시 강남구", "1층", Instant.now(), Instant.now()
            );

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(restaurantMapper.toDto(any(Restaurant.class))).willReturn(restaurantDto);
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

            // when
            RestaurantDto result = restaurantService.updateRestaurant(userId, restaurantId, updateRestaurantRequest);

            // then
            assertThat(result.name()).isEqualTo("새 맛집");
            assertThat(result.description()).isEqualTo("수정된 설명");
            
            then(restaurantMapper).should().toDto(any(Restaurant.class));
        }
        
        @Test
        @DisplayName("존재하지 않는 레스토랑 수정 시 예외가 발생한다.")
        void givenInvalidRestaurant_whenUpdate_thenThrowsException() {
            // given
            UpdateRestaurantRequest updateRestaurantRequest = new UpdateRestaurantRequest(
                    "새 맛집", "수정된 설명", "서울시 강남구", "1층", categoryId
            );

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> restaurantService.updateRestaurant(userId, restaurantId, updateRestaurantRequest))
                    .isInstanceOf(RestaurantNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("레스토랑 삭제")
    class Delete {
        @Test
        @DisplayName("정상적으로 레스토랑을 삭제한다.")
        void givenValidRequest_whenDelete_thenSuccess() {
            // given
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            
            // when
            restaurantService.deleteRestaurant(userId, restaurantId);
            
            // then
            then(restaurantRepository).should().delete(any(Restaurant.class));
        }
        
        @Test
        @DisplayName("존재하지 않는 레스토랑 삭제 시 예외가 발생한다.")
        void givenInvalidRestaurant_whenDelete_thenThrowsException() {
            // given
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());
            
            // when & then
            assertThatThrownBy(() -> restaurantService.deleteRestaurant(userId, restaurantId))
                    .isInstanceOf(RestaurantNotFoundException.class);
        }
        
        @Test
        @DisplayName("본인 소유가 아닌 레스토랑 삭제 시 예외가 발생한다.")
        void givenNotOwner_whenDelete_thenThrowsException() {
            // given
            UUID otherUserId = UUID.randomUUID();

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            
            // when & then
            assertThatThrownBy(() -> restaurantService.deleteRestaurant(otherUserId, restaurantId))
                    .isInstanceOf(RestaurantAccessDeniedException.class);
        }
    }
}