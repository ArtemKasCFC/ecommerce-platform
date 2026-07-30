package com.petproject.ecommerce.audit.repository;

import com.petproject.ecommerce.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}