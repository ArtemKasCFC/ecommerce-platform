package com.petproject.ecommerce.specs;

import com.petproject.ecommerce.config.PropertiesReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecs {

    private static final RequestSpecification DEFAULT_SPEC = new RequestSpecBuilder()
            .setBaseUri(PropertiesReader.get("api.base.url"))
            .setContentType(ContentType.JSON)
            .build();

    public static RequestSpecification defaultSpec() {
        return DEFAULT_SPEC;
    }
}
