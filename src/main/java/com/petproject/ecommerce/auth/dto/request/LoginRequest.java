package com.petproject.ecommerce.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginRequest {

    @NotBlank(message = "Request must contain email")
    @Pattern(
            regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$",
            message = "Invalid email"
    )
    private String email;

    @NotBlank(message = "Request must contain password")
    private String password;
}
