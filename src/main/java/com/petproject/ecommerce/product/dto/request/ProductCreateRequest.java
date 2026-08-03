package com.petproject.ecommerce.product.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
    @NotBlank(message = "Title must not be blank")
    private String title;
    @Positive(message = "Price must be positive")
    private Double price;
}
