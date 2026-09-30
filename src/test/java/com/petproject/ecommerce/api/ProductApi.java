package com.petproject.ecommerce.api;

import com.petproject.ecommerce.constants.ProductEndpoints;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.request.ProductUpdateRequest;
import com.petproject.ecommerce.product.dto.response.ProductResponse;
import com.petproject.ecommerce.specs.RequestSpecs;
import com.petproject.ecommerce.specs.ResponseSpec;
import io.restassured.common.mapper.TypeRef;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ProductApi {


    public static <T> T createProduct(ProductCreateRequest body, String token, Class<T> type, int sc) {
        return given(RequestSpecs.defaultSpec())
                .body(body)
                .auth().oauth2(token)
                .log().all()
                .when()
                .post(ProductEndpoints.PRODUCTS)
                .then()
                .log().all()
                .spec(ResponseSpec.defaultSpec(sc))
                .extract()
                .response()
                .as(type);
    }

    public static List<ProductResponse> getProducts(int sc) {
        return given(RequestSpecs.defaultSpec())
                .when()
                .log().all()
                .get(ProductEndpoints.PRODUCTS)
                .then()
                .log().all()
                .spec(ResponseSpec.defaultSpec(sc))
                .extract()
                .response()
                .as(new TypeRef<List<ProductResponse>>() {
                });
    }

    public static <T> T getProductById(Long id, Class<T> type, int sc) {
        return given(RequestSpecs.defaultSpec())
                .when()
                .log().all()
                .pathParam("id", id)
                .get(ProductEndpoints.PRODUCTS_BY_ID)
                .then()
                .log().all()
                .spec(ResponseSpec.defaultSpec(sc))
                .extract()
                .response()
                .as(type);
    }


    public static <T> T updateProduct(ProductUpdateRequest body, String token, Long id, Class<T> type, int sc) {
        return given(RequestSpecs.defaultSpec())
                .body(body)
                .auth().oauth2(token)
                .when()
                .pathParam("id", id)
                .log().all()
                .put(ProductEndpoints.PRODUCTS_BY_ID)
                .then()
                .log().all()
                .spec(ResponseSpec.defaultSpec(sc))
                .extract()
                .response()
                .as(type);
    }

    public static <T> T deleteProductById(Long id, String token, Class<T> type, int sc) {
        var response = given(RequestSpecs.defaultSpec())
                .auth().oauth2(token)
                .when()
                .log().all()
                .pathParam("id", id)
                .delete(ProductEndpoints.PRODUCTS_BY_ID)
                .then()
                .log().all()
                .statusCode(sc)
                .extract()
                .response();

        if (type == Void.class) {
            return null;
        }

        return response.as(type);
    }
}
