package com.petproject.ecommerce.product.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductUpdateRequest {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 50, message = "Title must not exceed 50 characters")
    private String title;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be positive")
    @DecimalMax(value = "10000", message = "Price must not exceed 10000")
    private BigDecimal price;
}