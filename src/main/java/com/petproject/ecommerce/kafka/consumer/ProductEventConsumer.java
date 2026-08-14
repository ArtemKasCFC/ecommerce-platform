package com.petproject.ecommerce.kafka.consumer;

import com.petproject.ecommerce.audit.service.AuditService;
import com.petproject.ecommerce.kafka.event.ProductEvent;
import com.petproject.ecommerce.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventConsumer {

    private final AuditService auditService;
    private final NotificationService notificationService;

    public ProductEventConsumer(AuditService auditService, NotificationService notificationService) {
        this.auditService = auditService;
        this.notificationService = notificationService;
    }


    @KafkaListener(topics = "product-events", groupId = "product-service-group")
    public void consume(ProductEvent event) {

        System.out.println("EVENT RECEIVED: " + event.getId() + " | thread: " + Thread.currentThread().getName());

        notificationService.notifyProductEvent(event);
        auditService.saveProductEvent(event);
    }
}