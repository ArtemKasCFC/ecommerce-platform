package com.petproject.ecommerce.factories;

import com.petproject.ecommerce.generators.ProductDataGenerator;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.request.ProductUpdateRequest;

public class ProductFactory {

    public static ProductCreateRequest defaultProduct() {
        return ProductCreateRequest
                .builder()
                .title(ProductDataGenerator.randomTitle())
                .price(ProductDataGenerator.randomPrice())
                .build();
    }

    public static ProductUpdateRequest defaultProductUpdate() {
        return ProductUpdateRequest
                .builder()
                .title(ProductDataGenerator.randomTitle())
                .price(ProductDataGenerator.randomPrice())
                .build();
    }
}
