package com.petproject.ecommerce.tests;

import com.petproject.ecommerce.assertions.NotificationAssertions;
import com.petproject.ecommerce.aws.SqsTestConsumer;
import com.petproject.ecommerce.database.NotificationsDb;
import com.petproject.ecommerce.kafka.ProductKafkaTestConsumer;
import com.petproject.ecommerce.kafka.event.ProductCreatedEvent;
import com.petproject.ecommerce.kafka.event.ProductDeletedEvent;
import com.petproject.ecommerce.kafka.event.ProductUpdatedEvent;
import com.petproject.ecommerce.notification.entity.Notification;
import com.petproject.ecommerce.product.dto.response.ProductResponse;
import com.petproject.ecommerce.steps.ProductSteps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class NotificationsTests {

    private SqsTestConsumer sqsConsumer;
    private ProductKafkaTestConsumer kafkaConsumer;

    @BeforeEach
    void setUpSqsQueue() {
        sqsConsumer = new SqsTestConsumer();
        sqsConsumer.initialize();
    }

    @BeforeEach
    void setUpConsumer() {
        kafkaConsumer = new ProductKafkaTestConsumer();
    }

    @AfterEach
    void closeConsumer() {
        kafkaConsumer.close();
    }

    @Test
    void shouldHandleProductCreatedEvent() {
        ProductResponse createdProduct = ProductSteps.sendDefaultCreateProductRequest();

        ProductCreatedEvent kafkaEvent = kafkaConsumer.read(
                createdProduct.getId(),
                ProductCreatedEvent.class,
                ofSeconds(3)).orElseThrow(() -> new RuntimeException("ProductCreatedEvent was not found")
        );

        ProductCreatedEvent sqsMessage = sqsConsumer.read(
                createdProduct.getId(),
                ProductCreatedEvent.class,
                ofSeconds(3)).orElseThrow(() -> new RuntimeException("SQS message was not found")
        );

        assertThat(sqsMessage)
                .usingRecursiveComparison()
                .isEqualTo(kafkaEvent);


        Notification notificationRecord = NotificationsDb.findByProductIdAndEventType(sqsMessage.getId(), sqsMessage.getEventType());
        NotificationAssertions.assertNotificationRecord(sqsMessage, notificationRecord);
    }


    @Test
    void shouldHandleProductUpdatedEvent() {
        ProductResponse updatedProduct = ProductSteps.sendDefaultUpdateProductRequest();


        ProductUpdatedEvent kafkaEvent = kafkaConsumer.read(
                updatedProduct.getId(),
                ProductUpdatedEvent.class,
                ofSeconds(3)).orElseThrow(() -> new RuntimeException("ProductUpdatedEvent was not found")
        );

        ProductUpdatedEvent sqsMessage = sqsConsumer.read(
                updatedProduct.getId(),
                ProductUpdatedEvent.class,
                ofSeconds(3)).orElseThrow(() -> new RuntimeException("SQS message was not found")
        );

        assertThat(sqsMessage)
                .usingRecursiveComparison()
                .isEqualTo(kafkaEvent);


        Notification notificationRecord = NotificationsDb.findByProductIdAndEventType(sqsMessage.getId(), sqsMessage.getEventType());
        NotificationAssertions.assertNotificationRecord(sqsMessage, notificationRecord);
    }


    @Test
    void shouldHandleProductDeletedEvent() {
        ProductResponse deletedProduct = ProductSteps.sendDefaultDeleteProductRequest();


        ProductDeletedEvent kafkaEvent = kafkaConsumer.read(
                deletedProduct.getId(),
                ProductDeletedEvent.class,
                ofSeconds(3)).orElseThrow(() -> new RuntimeException("ProductDeletedEvent was not found")
        );

        ProductDeletedEvent sqsMessage = sqsConsumer.read(
                deletedProduct.getId(),
                ProductDeletedEvent.class,
                ofSeconds(3)).orElseThrow(() -> new RuntimeException("SQS message was not found")
        );

        assertThat(sqsMessage)
                .usingRecursiveComparison()
                .isEqualTo(kafkaEvent);


        Notification notificationRecord = NotificationsDb.findByProductIdAndEventType(sqsMessage.getId(), sqsMessage.getEventType());
        NotificationAssertions.assertNotificationRecord(sqsMessage, notificationRecord);
    }
}
