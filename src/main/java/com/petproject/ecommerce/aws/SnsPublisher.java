package com.petproject.ecommerce.aws;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Component
public class SnsPublisher {

    private final SnsClient snsClient;

    public SnsPublisher(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void publish(String topicArn, String message) {

        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .build();

        PublishResponse response = snsClient.publish(request);
    }
}
