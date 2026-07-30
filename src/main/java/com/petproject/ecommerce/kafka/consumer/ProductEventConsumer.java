package com.petproject.ecommerce.kafka.consumer;

import com.petproject.ecommerce.audit.service.AuditService;
import com.petproject.ecommerce.kafka.event.ProductCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventConsumer {

    private final AuditService auditService;


    public ProductEventConsumer(AuditService auditService) {
        this.auditService = auditService;
    }


    @KafkaListener(topics = "product-events", groupId = "product-service-group")
    public void consume(ProductCreatedEvent event) {
        System.out.println("EVENT RECEIVED: " + event.getId());
        auditService.saveProductCreatedEvent(event.getId());
    }
}