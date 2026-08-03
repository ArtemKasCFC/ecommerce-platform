package com.petproject.ecommerce.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesReader {

    private static final Properties PROPERTIES = loadProperties();

    private PropertiesReader() {
    }

    private static Properties loadProperties() {

        Properties properties = new Properties();

        try (InputStream input = PropertiesReader.class
                .getClassLoader()
                .getResourceAsStream("test.properties")) {

            if (input == null) {
                throw new RuntimeException("test.properties not found");
            }

            properties.load(input);

            return properties;

        } catch (IOException e) {
            throw new RuntimeException("Cannot load test.properties", e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}