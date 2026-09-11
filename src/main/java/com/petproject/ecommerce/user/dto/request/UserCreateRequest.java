package com.petproject.ecommerce.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCreateRequest {
    @NotBlank(message = "Request must contain name")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Request must contain email")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Request must contain password")
    @Size(min = 8, max = 64, message = "Password's length must be between 8 and 64 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).*$",
            message = "Password must contain uppercase, lowercase, digit and special character"
    )
    private String password;
}
