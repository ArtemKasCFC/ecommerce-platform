package com.petproject.ecommerce.generators;

import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductDataGenerator {
    private static final Faker FAKER = new Faker();

    public static String randomTitle() {
        return FAKER.commerce().productName();
    }

    public static BigDecimal randomPrice() {
        return BigDecimal.valueOf(
                FAKER.number().randomDouble(2, 1, 10000)
        ).setScale(2, RoundingMode.HALF_UP);
    }
}
