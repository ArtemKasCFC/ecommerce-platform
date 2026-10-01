package com.petproject.ecommerce.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class RegistrationResponse {
    private UserResponse user;
    private String token;
}
