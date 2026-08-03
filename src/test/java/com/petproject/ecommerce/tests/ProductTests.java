package com.petproject.ecommerce.tests;

import com.petproject.ecommerce.api.ProductApi;
import com.petproject.ecommerce.database.ProductDb;
import com.petproject.ecommerce.factories.ProductFactory;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.response.ProductResponse;
import com.petproject.ecommerce.product.entity.Product;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTests {

    @Test
    void shouldGetProductById() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);
        ProductResponse receivedProduct = ProductApi.getProduct(createdProduct.getId(), ProductResponse.class, 200);

        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);

        Product productRecord = ProductDb.findById(receivedProduct.getId());

        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(productRecord);
    }
}
