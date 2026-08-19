package com.petproject.ecommerce.aws;

import com.petproject.ecommerce.kafka.event.ProductEvent;
import com.petproject.ecommerce.notification.dto.SnsNotification;
import com.petproject.ecommerce.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class SqsConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;
    private final NotificationService notificationService;

    public SqsConsumer(SqsClient sqsClient, ObjectMapper objectMapper, @Value("${aws.sqs.product-notifications-queue-url}") String queueUrl, NotificationService notificationService) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedDelay = 1000)
    public void receiveMessages() {

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(10)
                .build();

        ReceiveMessageResponse response = sqsClient.receiveMessage(request);

        for (Message message : response.messages()) {
            processMessage(message);
        }
    }

    private void processMessage(Message message) {

        SnsNotification snsNotification = objectMapper.readValue(message.body(), SnsNotification.class);
        ProductEvent event = objectMapper.readValue(snsNotification.message(), ProductEvent.class);

        notificationService.saveNotification(event);

        deleteMessage(message);
    }

    private void deleteMessage(Message message) {

        DeleteMessageRequest request = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();

        sqsClient.deleteMessage(request);

        log.info("SQS message deleted: {}", message.messageId());
    }
}