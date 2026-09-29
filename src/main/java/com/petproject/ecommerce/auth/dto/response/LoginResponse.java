package com.petproject.ecommerce.auth.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginResponse {
    private String token;
}
