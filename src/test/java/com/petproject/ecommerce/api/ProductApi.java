package com.petproject.ecommerce.api;

import com.petproject.ecommerce.enpoints.ProductEndpoints;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.response.ProductResponse;
import com.petproject.ecommerce.specs.RequestSpecs;
import com.petproject.ecommerce.specs.ResponseSpec;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ProductApi {

    public static <T> T getProduct(Long id, Class<T> type, int sc) {
        return given(RequestSpecs.defaultSpec())
                .when()
                .pathParam("id", id)
                .get(ProductEndpoints.PRODUCTS_BY_ID)
                .then()
                .spec(ResponseSpec.defaultSpec(sc))
                .extract()
                .response()
                .as(type);
    }

    public static <T> T createProduct(ProductCreateRequest body, Class<T> type, int sc) {
        return given(RequestSpecs.defaultSpec())
                .body(body)
                .when()
                .post(ProductEndpoints.PRODUCTS)
                .then()
                .spec(ResponseSpec.defaultSpec(sc))
                .extract()
                .response()
                .as(type);
    }
}
