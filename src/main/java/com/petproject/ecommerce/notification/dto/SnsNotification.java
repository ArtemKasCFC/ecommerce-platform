package com.petproject.ecommerce.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SnsNotification(
        @JsonProperty("Type")
        String type,
        @JsonProperty("MessageId")
        String messageId,
        @JsonProperty("TopicArn")
        String topicArn,
        @JsonProperty("Message")
        String message,
        @JsonProperty("Timestamp")
        String timestamp
) {
}