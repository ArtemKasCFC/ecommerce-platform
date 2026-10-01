package com.petproject.ecommerce.assertions;

import com.petproject.ecommerce.user.dto.request.UserCreateRequest;
import com.petproject.ecommerce.user.dto.response.RegistrationResponse;
import com.petproject.ecommerce.user.enums.UserStatuses;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserAssertions {

    public static void validateUserCreateResponse(RegistrationResponse createdUser, UserCreateRequest body) {
        assertThat(createdUser.getUser().getId()).isPositive();
        assertThat(createdUser.getToken()).isNotBlank();
        assertThat(createdUser.getUser().getName()).isEqualTo(body.getName());
        assertThat(createdUser.getUser().getEmail()).isEqualTo(body.getEmail());
        assertThat(createdUser.getUser().getStatus()).isEqualTo(UserStatuses.ACTIVE);
        assertThat(createdUser.getUser().getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now()).isAfter(LocalDateTime.now().minusSeconds(10));
    }
}
