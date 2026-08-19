package com.petproject.ecommerce.assertions;

import com.petproject.ecommerce.kafka.event.ProductCreatedEvent;
import com.petproject.ecommerce.kafka.event.ProductEvent;
import com.petproject.ecommerce.kafka.event.ProductUpdatedEvent;
import com.petproject.ecommerce.notification.entity.Notification;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationAssertions {

    public static void assertNotificationRecord(ProductEvent sqsMessage, Notification notificationRecord) {

        assertThat(notificationRecord.getId()).isPositive();
        assertThat(notificationRecord.getProductId()).isEqualTo(sqsMessage.getId());
        assertThat(notificationRecord.getEventType()).isEqualTo(sqsMessage.getEventType());

        if (sqsMessage instanceof ProductCreatedEvent createdMessage) {

            assertThat(notificationRecord.getTitle()).isEqualTo(createdMessage.getTitle());
            assertThat(notificationRecord.getPrice()).isEqualTo(createdMessage.getPrice());

        } else if (sqsMessage instanceof ProductUpdatedEvent updatedMessage) {

            assertThat(notificationRecord.getTitle()).isEqualTo(updatedMessage.getTitle());
            assertThat(notificationRecord.getPrice()).isEqualTo(updatedMessage.getPrice());

        } else {

            assertThat(notificationRecord.getTitle()).isNull();
            assertThat(notificationRecord.getPrice()).isNull();

        }

        assertThat(notificationRecord.getReceivedAt()).isInThePast();

    }
}
