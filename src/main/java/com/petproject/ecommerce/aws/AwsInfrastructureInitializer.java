package com.petproject.ecommerce.aws;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@Slf4j
@Component
public class AwsInfrastructureInitializer {

    private final SnsClient snsClient;
    private final SqsClient sqsClient;

    public AwsInfrastructureInitializer(SnsClient snsClient, SqsClient sqsClient) {
        this.snsClient = snsClient;
        this.sqsClient = sqsClient;
    }

    @PostConstruct
    public void initialize() {

        String topicName = "product-notifications";
        CreateTopicRequest request = CreateTopicRequest.builder()
                .name(topicName)
                .build();

        CreateTopicResponse response = snsClient.createTopic(request);
        log.info("SNS topic '{}' is ready. ARN: {}", topicName, response.topicArn());

        String queueUrl = createQueue();
        log.info("SQS queue is ready. URL: {}", queueUrl);

        String queueArn = getQueueArn(queueUrl);
        log.info("SQS queue ARN: {}", queueArn);

        if (!subscriptionExists(response.topicArn(), queueArn)) {
            log.info("SNS → SQS subscription does not exist. Creating...");
            createSubscription(response.topicArn(), queueArn);
        } else {
            log.info("SNS → SQS subscription already exists. Skipping creation.");
        }
    }

    private String createQueue() {

        String queueName = "product-notifications-queue";

        CreateQueueRequest request = CreateQueueRequest.builder()
                .queueName(queueName)
                .build();

        CreateQueueResponse response = sqsClient.createQueue(request);

        return response.queueUrl();
    }

    private String getQueueArn(String queueUrl) {

        GetQueueAttributesRequest request = GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.QUEUE_ARN)
                .build();

        GetQueueAttributesResponse response = sqsClient.getQueueAttributes(request);

        return response.attributes().get(QueueAttributeName.QUEUE_ARN);
    }

    private void createSubscription(String topicArn, String queueArn) {

        SubscribeRequest request = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("sqs")
                .endpoint(queueArn)
                .build();

        SubscribeResponse response = snsClient.subscribe(request);

        log.info("SNS subscription is ready. ARN: {}", response.subscriptionArn());
    }

    private boolean subscriptionExists(String topicArn, String queueArn) {

        ListSubscriptionsByTopicRequest request =
                ListSubscriptionsByTopicRequest.builder()
                        .topicArn(topicArn)
                        .build();

        ListSubscriptionsByTopicResponse response =
                snsClient.listSubscriptionsByTopic(request);

        log.info("Subscriptions: {}", response.subscriptions());

        return response.subscriptions()
                .stream()
                .anyMatch(subscription -> "sqs".equals(subscription.protocol()) && queueArn.equals(subscription.endpoint()));
    }
}
