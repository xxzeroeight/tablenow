package com.tablenow.tablenow.domain.restaurant;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablenow.tablenow.domain.category.entity.Category;
import com.tablenow.tablenow.domain.category.repository.CategoryRepository;
import com.tablenow.tablenow.domain.restaurant.dto.request.CreateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.dto.request.UpdateRestaurantRequest;
import com.tablenow.tablenow.domain.restaurant.entity.Restaurant;
import com.tablenow.tablenow.domain.restaurant.repository.RestaurantRepository;
import com.tablenow.tablenow.domain.user.entity.Role;
import com.tablenow.tablenow.domain.user.entity.User;
import com.tablenow.tablenow.domain.user.repository.UserRepository;
import com.tablenow.tablenow.global.security.CustomUserDetails;
import com.tablenow.tablenow.support.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("Restaurant Integration Test")
public class RestaurantIntegrationTest
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private CategoryRepository categoryRepository;

    private UUID categoryId;

    @BeforeEach
    void setUp() {
        Category category = categoryRepository.save(
                Category.builder()
                        .name("한식")
                        .build()
        );

        categoryId = category.getId();
    }

    private UUID currentUserId() {
        CustomUserDetails details = (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        return details.getUserId();
    }

    private User createOtherOwner() {
        String id = UUID.randomUUID().toString().substring(0, 8);

        return userRepository.saveAndFlush(User.builder()
                .name("다른사장")
                .email(id + "@other.com")
                .username("other_" + id)
                .password("encoded")
                .phoneNumber("010" + Math.abs(id.hashCode()) % 100000000)
                .role(Role.OWNER)
                .build());
    }

    private UUID createRestaurantFor(User owner) {
        return restaurantRepository.save(Restaurant.builder()
                .name("맛집")
                .description("설명")
                .address("서울시 강남구")
                .addressDetail("1층")
                .user(owner)
                .category(categoryRepository.findById(categoryId).orElseThrow())
                .build()).getId();
    }

    @Nested
    @DisplayName("레스토랑 생성")
    class Create {
        @Test
        @WithMockCustomUser(role = "OWNER")
        @DisplayName("OWNER 권한으로 레스토랑을 생성한다.")
        void givenOwner_whenCreate_thenReturn201() throws Exception {
            // given
            CreateRestaurantRequest createRestaurantRequest = new CreateRestaurantRequest(
                    "맛집", "설명", "서울시 강남구", "1층", categoryId
            );
            
            // when & then
            mockMvc.perform(post("/api/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRestaurantRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("맛집"));
        }

        @Test
        @DisplayName("미인증 요청 시 401을 반환한다")
        void givenUnauthenticated_whenCreate_thenReturn401() throws Exception {
            // given
            CreateRestaurantRequest createRestaurantRequest = new CreateRestaurantRequest(
                    "맛집", "설명", "서울시 강남구", "1층", categoryId
            );

            // when & then
            mockMvc.perform(post("/api/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRestaurantRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockCustomUser(role = "USER")
        @DisplayName("USER 권한으로 생성 시 403을 반환한다")
        void givenUser_whenCreate_thenReturn403() throws Exception {
            // given
            CreateRestaurantRequest createRestaurantRequest = new CreateRestaurantRequest(
                    "맛집", "설명", "서울시 강남구", "1층", categoryId
            );

            // when & then
            mockMvc.perform(post("/api/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRestaurantRequest)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("레스토랑 수정")
    class Update {
        @Test
        @WithMockCustomUser(role = "OWNER")
        @DisplayName("본인 레스토랑을 수정한다")
        void givenOwner_whenUpdate_thenReturn200() throws Exception {
            // given
            User owner = userRepository.findById(currentUserId()).orElseThrow();
            UUID restaurantId = createRestaurantFor(owner);

            UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                    "새이름", null, null, null, categoryId
            );

            // when & then
            mockMvc.perform(patch("/api/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("새이름"));
        }

        @Test
        @WithMockCustomUser(role = "OWNER")
        @DisplayName("다른 OWNER의 레스토랑 수정 시 403을 반환한다")
        void givenOtherOwner_whenUpdate_thenReturn403() throws Exception {
            // given
            User otherOwner = createOtherOwner();
            UUID restaurantId = createRestaurantFor(otherOwner);

            UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                    "새이름", null, null, null, categoryId
            );

            // when & then
            mockMvc.perform(patch("/api/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("레스토랑 삭제")
    class Delete {
        @Test
        @WithMockCustomUser(role = "OWNER")
        @DisplayName("본인 레스토랑을 삭제한다")
        void givenOwner_whenDelete_thenReturn204() throws Exception {
            // given
            User owner = userRepository.findById(currentUserId()).orElseThrow();
            UUID restaurantId = createRestaurantFor(owner);

            // when & then
            mockMvc.perform(delete("/api/restaurants/{id}", restaurantId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockCustomUser(role = "OWNER")
        @DisplayName("다른 OWNER의 레스토랑 삭제 시 403을 반환한다")
        void givenOtherOwner_whenDelete_thenReturn403() throws Exception {
            // given
            User otherOwner = createOtherOwner();
            UUID restaurantId = createRestaurantFor(otherOwner);

            // when & then
            mockMvc.perform(delete("/api/restaurants/{id}", restaurantId))
                    .andExpect(status().isForbidden());
        }
    }
}
