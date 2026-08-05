package com.petproject.ecommerce.generators;

import com.github.javafaker.Faker;

import java.math.BigDecimal;

public class ProductDataGenerator {
    private static final Faker FAKER = new Faker();

    public static String randomTitle() {
        return FAKER.commerce().productName();
    }

    public static BigDecimal randomPrice() {
        return BigDecimal.valueOf(FAKER.number().randomDouble(2, 1, 10000));
    }
}
