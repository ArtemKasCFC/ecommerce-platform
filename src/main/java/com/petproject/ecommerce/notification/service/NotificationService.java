package com.petproject.ecommerce.notification.service;

import com.petproject.ecommerce.aws.SnsPublisher;
import com.petproject.ecommerce.kafka.event.ProductCreatedEvent;
import com.petproject.ecommerce.kafka.event.ProductEvent;
import com.petproject.ecommerce.kafka.event.ProductUpdatedEvent;
import com.petproject.ecommerce.notification.entity.Notification;
import com.petproject.ecommerce.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;


@Service
public class NotificationService {

    private final SnsPublisher snsPublisher;
    private final ObjectMapper objectMapper;
    private final String topicArn;
    private final NotificationRepository notificationRepository;

    public NotificationService(SnsPublisher snsPublisher, ObjectMapper objectMapper, @Value("${aws.sns.product-notifications-topic-arn}") String topicArn, NotificationRepository notificationRepository) {
        this.snsPublisher = snsPublisher;
        this.objectMapper = objectMapper;
        this.topicArn = topicArn;
        this.notificationRepository = notificationRepository;
    }

    public void notifyProductEvent(ProductEvent event) {
        String message = objectMapper.writeValueAsString(event);
        snsPublisher.publish(topicArn, message);
    }

    public void saveNotification(ProductEvent event) {
        Notification notification = new Notification();

        notification.setEventType(event.getEventType());
        notification.setProductId(event.getId());
        notification.setReceivedAt(LocalDateTime.now());


        if (event instanceof ProductCreatedEvent createdEvent) {
            notification.setTitle(createdEvent.getTitle());
            notification.setPrice(createdEvent.getPrice());
        }

        if (event instanceof ProductUpdatedEvent updatedEvent) {
            notification.setTitle(updatedEvent.getTitle());
            notification.setPrice(updatedEvent.getPrice());
        }

        notificationRepository.save(notification);
    }

}
