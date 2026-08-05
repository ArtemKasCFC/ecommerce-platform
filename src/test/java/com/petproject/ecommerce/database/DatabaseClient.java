package com.petproject.ecommerce.database;

import com.petproject.ecommerce.config.PropertiesReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseClient {

    private DatabaseClient() {
    }

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(
                    PropertiesReader.get("db.url"),
                    PropertiesReader.get("db.username"),
                    PropertiesReader.get("db.password")
            );

        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to database", e);
        }
    }
}