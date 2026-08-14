package com.petproject.ecommerce.kafka.producer;

import com.petproject.ecommerce.kafka.event.ProductEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductEventProducer {

    private static final String TOPIC = "product-events";
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductEventProducer(KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ProductEvent event) {
        kafkaTemplate.send(TOPIC, event.getId().toString(), event);
    }

}