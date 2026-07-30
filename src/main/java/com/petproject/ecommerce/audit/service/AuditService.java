package com.petproject.ecommerce.audit.service;

import com.petproject.ecommerce.audit.entity.AuditLog;
import com.petproject.ecommerce.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void saveProductCreatedEvent(Long productId) {

        AuditLog auditLog = new AuditLog(
                "PRODUCT_CREATED",
                productId,
                LocalDateTime.now()
        );

        auditLogRepository.save(auditLog);
    }
}