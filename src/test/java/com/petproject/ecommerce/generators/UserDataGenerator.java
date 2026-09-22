package com.petproject.ecommerce.generators;

import com.github.javafaker.Faker;

public class UserDataGenerator {

    private static final Faker FAKER = new Faker();

    public static String randomName() {
        return FAKER.name().fullName();
    }

    public static String randomEmail() {
        return FAKER.internet().emailAddress();
    }

    public static String randomPassword() {
        return FAKER.internet().password(8, 64, true, true, true);
    }
}
