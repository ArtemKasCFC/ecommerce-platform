package com.petproject.ecommerce.aws;

import com.petproject.ecommerce.kafka.event.ProductEvent;
import com.petproject.ecommerce.notification.dto.SnsNotification;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
public class SqsTestConsumer {

    private static final String TEST_QUEUE_NAME = "product-notifications-test-queue";

    private final SqsClient sqsClient;
    private final SnsClient snsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();


    private String queueUrl;

    public SqsTestConsumer() {
        this.sqsClient = AwsTestConfig.sqsClient();
        this.snsClient = AwsTestConfig.snsClient();
    }

    public void initialize() {

        String topicArn = findTopicArn();

        queueUrl = createQueue();

        String queueArn = getQueueArn(queueUrl);

        if (!subscriptionExists(topicArn, queueArn)) {
            createSubscription(topicArn, queueArn);
        }

        log.info("Test SQS consumer initialized. Queue URL: {}", queueUrl);
    }

    private String findTopicArn() {

        return snsClient.listTopics()
                .topics()
                .stream()
                .map(Topic::topicArn)
                .filter(arn -> arn.endsWith(":product-notifications"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("SNS topic 'product-notifications' not found"));
    }

    private String createQueue() {

        CreateQueueResponse response = sqsClient.createQueue(
                CreateQueueRequest.builder()
                        .queueName(TEST_QUEUE_NAME)
                        .build()
        );

        return response.queueUrl();
    }

    private String getQueueArn(String queueUrl) {

        GetQueueAttributesResponse response =
                sqsClient.getQueueAttributes(
                        GetQueueAttributesRequest.builder()
                                .queueUrl(queueUrl)
                                .attributeNames(QueueAttributeName.QUEUE_ARN)
                                .build()
                );

        return response.attributes().get(QueueAttributeName.QUEUE_ARN);
    }

    private boolean subscriptionExists(String topicArn, String queueArn) {

        ListSubscriptionsByTopicResponse response =
                snsClient.listSubscriptionsByTopic(
                        ListSubscriptionsByTopicRequest.builder()
                                .topicArn(topicArn)
                                .build()
                );

        return response.subscriptions()
                .stream()
                .anyMatch(subscription ->
                        "sqs".equals(subscription.protocol())
                                && queueArn.equals(subscription.endpoint())
                );
    }

    private void createSubscription(String topicArn, String queueArn) {

        SubscribeResponse response = snsClient.subscribe(
                SubscribeRequest.builder()
                        .topicArn(topicArn)
                        .protocol("sqs")
                        .endpoint(queueArn)
                        .build()
        );

        log.info("Test SNS → SQS subscription created: {}", response.subscriptionArn());
    }

    public List<Message> receiveMessages() {

        ReceiveMessageResponse response =
                sqsClient.receiveMessage(
                        ReceiveMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .maxNumberOfMessages(10)
                                .waitTimeSeconds(10)
                                .build()
                );

        return response.messages();
    }

    public void sendMessage(String messageBody) {

        sqsClient.sendMessage(
                SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(messageBody)
                        .build()
        );
    }

    public void sendEvent(ProductEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);

            SnsNotification notification = new SnsNotification(
                    "Notification",
                    UUID.randomUUID().toString(),
                    findTopicArn(),
                    eventJson,
                    LocalDateTime.now().toString()
            );

            sendMessage(objectMapper.writeValueAsString(notification));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}