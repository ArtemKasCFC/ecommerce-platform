package com.petproject.ecommerce.database;

import com.petproject.ecommerce.product.entity.Product;
import org.springframework.jdbc.core.JdbcTemplate;

public class ProductsDb {

    private static final JdbcTemplate jdbcTemplate =
            DatabaseClient.getJdbcTemplate();

    public static Product findById(long id) {

        String sql = """
                SELECT id,
                       title,
                       price
                FROM products
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> Product.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .price(rs.getBigDecimal("price"))
                        .build(),
                id);
    }

    public static boolean existsById(long id) {

        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM products
                    WHERE id = ?
                )
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }
}