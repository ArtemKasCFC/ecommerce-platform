package com.petproject.ecommerce.database;

import com.petproject.ecommerce.notification.entity.Notification;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationsDb {

    private static final JdbcTemplate jdbcTemplate = DatabaseClient.getJdbcTemplate();

    public static Notification findByProductIdAndEventType(long id, String eventType) {

        String sql = """
                SELECT id,
                       product_id,
                       event_type,
                       title,
                       price,
                       received_at
                FROM notifications
                WHERE product_id = ? AND event_type = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> Notification.builder()
                        .id(rs.getLong("id"))
                        .productId(rs.getLong("product_id"))
                        .eventType(rs.getString("event_type"))
                        .title(rs.getString("title"))
                        .price(rs.getBigDecimal("price"))
                        .receivedAt(rs.getTimestamp("received_at").toLocalDateTime())
                        .build(),
                id, eventType);
    }

    public static boolean existsByProductId(long id) {

        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM notifications
                    WHERE product_id = ?
                )
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }
}
