package com.petproject.ecommerce.generators;

import com.github.javafaker.Faker;

public class ProductDataGenerator {
    private static final Faker FAKER = new Faker();

    public static String randomTitle() {
        return FAKER.commerce().productName();
    }

    public static Double randomPrice() {
        return FAKER.number().randomDouble(2, 1, 10000);
    }
}
