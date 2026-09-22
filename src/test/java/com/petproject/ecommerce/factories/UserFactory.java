package com.petproject.ecommerce.factories;

import com.petproject.ecommerce.generators.UserDataGenerator;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;

public class UserFactory {

    public static UserCreateRequest defaultUser() {
        return UserCreateRequest.builder()
                .name(UserDataGenerator.randomName())
                .email(UserDataGenerator.randomEmail())
                .password(UserDataGenerator.randomPassword())
                .build();
    }
}
