package com.petproject.ecommerce.database;

import com.petproject.ecommerce.user.entity.User;
import com.petproject.ecommerce.user.enums.Roles;
import com.petproject.ecommerce.user.enums.Statuses;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

public class UsersDb {

    private static final JdbcTemplate jdbcTemplate = DatabaseClient.getJdbcTemplate();

    public static User findById(long id) {

        String sql = """
                SELECT id,
                       created_at,
                       email,
                       name,
                       password,
                       role,
                       status
                FROM users
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> User.builder()
                        .id(rs.getLong("id"))
                        .createdAt(rs.getObject("created_at", LocalDateTime.class))
                        .email(rs.getString("email"))
                        .name(rs.getString("name"))
                        .password(rs.getString("password"))
                        .role(Roles.valueOf(rs.getString("role")))
                        .status(Statuses.valueOf(rs.getString("status")))
                        .build(),
                id);
    }

    public static boolean existsById(long id) {

        String sql = """
                SELECT EXISTS (
                SELECT 1
                FROM users
                WHERE id = ?
                )
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }
}
