package com.petproject.ecommerce.assertions;

import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import com.petproject.ecommerce.user.dto.response.UserResponse;
import com.petproject.ecommerce.user.enums.Statuses;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserAssertions {

    public static void validateUserCreateResponse(UserResponse createdUser, UserCreateRequest body) {
        assertThat(createdUser.getId()).isPositive();
        assertThat(createdUser.getName()).isEqualTo(body.getName());
        assertThat(createdUser.getEmail()).isEqualTo(body.getEmail());
        assertThat(createdUser.getStatus()).isEqualTo(Statuses.ACTIVE);
        assertThat(createdUser.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now()).isAfter(LocalDateTime.now().minusSeconds(10));
    }
}
