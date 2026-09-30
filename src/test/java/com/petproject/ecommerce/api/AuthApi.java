package com.petproject.ecommerce.api;

import com.petproject.ecommerce.auth.dto.request.LoginRequest;
import com.petproject.ecommerce.constants.AuthEndpoints;
import com.petproject.ecommerce.specs.RequestSpecs;
import com.petproject.ecommerce.specs.ResponseSpec;

import static io.restassured.RestAssured.given;

public class AuthApi {

    public static <T> T login(LoginRequest body, Class<T> type, int sc) {
        return given(RequestSpecs.defaultSpec())
                .body(body)
                .log().all()
                .when()
                .post(AuthEndpoints.LOGIN)
                .then()
                .log().all()
                .spec(ResponseSpec.defaultSpec(sc))
                .extract()
                .response()
                .as(type);
    }
}
