package com.petproject.ecommerce.database;

import com.petproject.ecommerce.product.entity.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDb {
    public static Product findById(long id) {

        String sql = """
                SELECT id,
                       title,
                       price
                FROM products
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseClient.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Product.builder()
                        .id(resultSet.getLong("id"))
                        .title(resultSet.getString("title"))
                        .price(resultSet.getDouble("price"))
                        .build();
            }

            throw new RuntimeException("Product not found. Id = " + id);

        } catch (SQLException e) {
            throw new RuntimeException("Cannot execute SQL query", e);
        }
    }
}
