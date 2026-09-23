package com.petproject.ecommerce.api;

import com.petproject.ecommerce.constants.UserEnpoints;
import com.petproject.ecommerce.specs.RequestSpecs;
import com.petproject.ecommerce.specs.ResponseSpec;
import com.petproject.ecommerce.user.dto.request.UserCreateRequest;

import static io.restassured.RestAssured.given;

public class UserApi {

    public static <T> T createUser(UserCreateRequest body, Class<T> type, int sc) {
        return given(RequestSpecs.defaultSpec())
                .body(body)
                .when()
                .post(UserEnpoints.USERS)
                .then()
                .log().all()
                .spec(ResponseSpec.defaultSpec(sc))
                .extract()
                .response()
                .as(type);
    }
}
