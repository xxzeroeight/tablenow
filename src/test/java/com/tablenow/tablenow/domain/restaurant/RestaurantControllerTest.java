package com.tablenow.tablenow.domain.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablenow.tablenow.domain.restaurant.dto.entity.RestaurantDto;
import com.tablenow.tablenow.domain.restaurant.dto.request.CreateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.dto.request.UpdateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.service.RestaurantService;
import com.tablenow.tablenow.domain.user.entity.Role;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.global.security.CustomUserDetails;
import com.tablenow.tablenow.global.security.CustomUserDetailsService;
import com.tablenow.tablenow.global.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(RestaurantController.class)
@DisplayName("RestaurantController Slice Test")
class RestaurantControllerTest
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RestaurantService restaurantService;
    @MockitoBean private JwtProvider jwtProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    private UUID restaurantId;
    private UUID categoryId;

    private RestaurantDto restaurantDto;

    private CustomUserDetails mockUserDetails() {
        User user = User.builder()
                .name("사장님")
                .email("owner@test.com")
                .username("owner")
                .password("Test1234!!")
                .phoneNumber("01012345678")
                .role(Role.OWNER)
                .build();

        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        return new CustomUserDetails(user);
    }

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        
        restaurantDto = new RestaurantDto(
                restaurantId,
                "맛집",
                "설명",
                "서울시 강남구",
                "1층",
                Instant.now(),
                Instant.now()
        );
    }

    @BeforeEach
    void setUpSecurity() {
        CustomUserDetails userDetails = mockUserDetails();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                )
        );
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }
    
    @Nested
    @DisplayName("레스토랑 생성")
    class Create {
        @Test
        @DisplayName("POST /api/restaurants - 레스토랑 생성 시 201 응답")
        void givenValidRequest_whenCreate_thenReturns201() throws Exception {
            // given
            CreateRestaurantRequest createRestaurantRequest = new CreateRestaurantRequest(
                    "맛집", "설명", "서울시 강남구", "1층", categoryId
            );

            given(restaurantService.createRestaurant(any(UUID.class), any(CreateRestaurantRequest.class)))
                    .willReturn(restaurantDto);
            
            // when & then
            mockMvc.perform(post("/api/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRestaurantRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("맛집"));
        }
    }

    @Nested
    @DisplayName("레스토랑 수정")
    class Update {
        @Test
        @DisplayName("PATCH /api/restaurants/{id} - 레스토랑 수정 시 200 응답")
        void givenValidRequest_whenUpdate_thenReturns200() throws Exception {
            // given
            UpdateRestaurantRequest updateRestaurantRequest = new UpdateRestaurantRequest(
                    "새 맛집", "수정된 설명", "서울시 강남구", "1층", categoryId
            );

            RestaurantDto updateRestaurantDto = new RestaurantDto(
                    restaurantId, "새 맛집", "수정된 설명", "서울시 강남구", "1층",
                    Instant.now(), Instant.now()
            );

            given(restaurantService.updateRestaurant(any(UUID.class), eq(restaurantId), any(UpdateRestaurantRequest.class))).willReturn(updateRestaurantDto);
            
            // when & then
            mockMvc.perform(patch("/api/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRestaurantRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("새 맛집"));
        }
    }
    
    @Nested
    @DisplayName("레스토랑 삭제")
    class Delete {
        @Test
        @DisplayName("DELETE /api/restaurants/{id} - 레스토랑 삭제 시 204 응답")
        void givenValidRequest_whenDelete_thenReturns204() throws Exception {
            // given
            willDoNothing().given(restaurantService).deleteRestaurant(any(UUID.class), eq(restaurantId));
            
            // when & then
            mockMvc.perform(delete("/api/restaurants/{id}", restaurantId))
                    .andExpect(status().isNoContent());
        }
    }
}