package com.petproject.ecommerce.steps;

import com.petproject.ecommerce.api.ProductApi;
import com.petproject.ecommerce.factories.ProductFactory;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.request.ProductUpdateRequest;
import com.petproject.ecommerce.product.dto.response.ProductResponse;

import java.util.function.Consumer;

public class ProductSteps {

    public static ProductResponse sendDefaultCreateProductRequest() {
        String adminToken = UserSteps.createAdmin().get("token");
        ProductCreateRequest body = ProductFactory.defaultProduct();

        return ProductApi.createProduct(body, adminToken, ProductResponse.class, 201);
    }

    public static <T> T sendCreateProductRequest(Consumer<ProductCreateRequest> customizer, Class<T> type, int statusCode) {
        String adminToken = UserSteps.createAdmin().get("token");
        ProductCreateRequest body = ProductFactory.defaultProduct();
        customizer.accept(body);

        return ProductApi.createProduct(body, adminToken, type, statusCode);
    }

    public static ProductResponse sendDefaultUpdateProductRequest() {
        String adminToken = UserSteps.createAdmin().get("token");
        ProductResponse createdProduct = sendDefaultCreateProductRequest();
        ProductUpdateRequest updateRequestBody = ProductFactory.defaultProductUpdate();

        return ProductApi.updateProduct(updateRequestBody, adminToken, createdProduct.getId(), ProductResponse.class, 200);
    }

    public static ProductResponse sendDefaultDeleteProductRequest() {
        String adminToken = UserSteps.createAdmin().get("token");
        ProductResponse createdProduct = sendDefaultCreateProductRequest();
        ProductApi.deleteProductById(createdProduct.getId(), adminToken, Void.class, 204);

        return createdProduct;
    }
}
