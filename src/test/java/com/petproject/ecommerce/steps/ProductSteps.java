package com.petproject.ecommerce.steps;

import com.petproject.ecommerce.api.ProductApi;
import com.petproject.ecommerce.factories.ProductFactory;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.request.ProductUpdateRequest;
import com.petproject.ecommerce.product.dto.response.ProductResponse;

import java.util.function.Consumer;

public class ProductSteps {

    public static ProductResponse sendDefaultCreateProductRequest() {
        ProductCreateRequest body = ProductFactory.defaultProduct();

        return ProductApi.createProduct(body, ProductResponse.class, 201);
    }

    public static <T> T sendCreateProductRequest(Consumer<ProductCreateRequest> customizer, Class<T> type, int statusCode) {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        customizer.accept(body);

        return ProductApi.createProduct(body, type, statusCode);
    }

    public static ProductResponse sendDefaultUpdateProductRequest() {
        ProductResponse createdProduct = sendDefaultCreateProductRequest();
        ProductUpdateRequest updateRequestBody = ProductFactory.defaultProductUpdate();

        return ProductApi.updateProduct(updateRequestBody, createdProduct.getId(), ProductResponse.class, 200);
    }

    public static ProductResponse sendDefaultDeleteProductRequest() {
        ProductResponse createdProduct = sendDefaultCreateProductRequest();
        ProductApi.deleteProductById(createdProduct.getId(), Void.class, 204);
        
        return createdProduct;
    }
}
