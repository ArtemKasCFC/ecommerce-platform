package com.petproject.ecommerce.database;

//
//public class ProductDb {
//    public static Product findById(long id) {
//
//        String sql = """
//                SELECT id,
//                       title,
//                       price
//                FROM products
//                WHERE id = ?
//                """;
//
//        try (
//                Connection connection = DatabaseClient.getConnection();
//                PreparedStatement statement = connection.prepareStatement(sql)
//        ) {
//
//            statement.setLong(1, id);
//
//            ResultSet resultSet = statement.executeQuery();
//
//            if (resultSet.next()) {
//                return Product.builder()
//                        .id(resultSet.getLong("id"))
//                        .title(resultSet.getString("title"))
//                        .price(resultSet.getBigDecimal("price"))
//                        .build();
//            }
//
//            throw new RuntimeException("Product not found. Id = " + id);
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Cannot execute SQL query", e);
//        }
//    }
//}

import com.petproject.ecommerce.product.entity.Product;
import org.springframework.jdbc.core.JdbcTemplate;

public class ProductDb {

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