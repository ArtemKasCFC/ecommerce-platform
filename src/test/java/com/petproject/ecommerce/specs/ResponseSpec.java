package com.petproject.ecommerce.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpec {

    public static ResponseSpecification defaultSpec(int sc){
        return new ResponseSpecBuilder()
                .expectStatusCode(sc)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
