package com.petproject.ecommerce.audit.service;

import com.petproject.ecommerce.audit.entity.AuditLog;
import com.petproject.ecommerce.audit.repository.AuditLogRepository;
import com.petproject.ecommerce.kafka.event.ProductEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void saveProductEvent(ProductEvent event) {

        AuditLog auditLog = new AuditLog(
                event.getEventType(),
                event.getId(),
                LocalDateTime.now()
        );

        auditLogRepository.save(auditLog);
    }
}