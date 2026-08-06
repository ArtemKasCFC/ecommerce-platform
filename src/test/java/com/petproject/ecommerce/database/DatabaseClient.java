package com.petproject.ecommerce.database;

import com.petproject.ecommerce.config.PropertiesReader;
import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

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