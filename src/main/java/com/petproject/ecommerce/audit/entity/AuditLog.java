package com.petproject.ecommerce.audit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;
    private Long productId;
    private LocalDateTime createdAt;

    public AuditLog(String eventType, Long productId, LocalDateTime createdAt) {
        this.eventType = eventType;
        this.productId = productId;
        this.createdAt = createdAt;
    }
}