package com.petproject.ecommerce.user.dto.response;

import com.petproject.ecommerce.user.enums.Statuses;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Statuses status;
    private LocalDateTime createdAt;
}
