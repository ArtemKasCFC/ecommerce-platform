package com.petproject.ecommerce.database;

import com.petproject.ecommerce.config.PropertiesReader;
import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

//
//public final class DatabaseClient {
//
//    private DatabaseClient() {
//    }
//
//    public static Connection getConnection() {
//
//        try {
//            return DriverManager.getConnection(
//                    PropertiesReader.get("db.url"),
//                    PropertiesReader.get("db.username"),
//                    PropertiesReader.get("db.password")
//            );
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Cannot connect to database", e);
//        }
//    }
//}
public class DatabaseClient {

    @Getter
    private static final JdbcTemplate jdbcTemplate;

    static {

        DriverManagerDataSource dataSource =
                new DriverManagerDataSource();

        dataSource.setUrl(PropertiesReader.get("db.url"));
        dataSource.setUsername(PropertiesReader.get("db.username"));
        dataSource.setPassword(PropertiesReader.get("db.password"));

        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
}