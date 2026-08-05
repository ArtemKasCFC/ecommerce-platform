package com.petproject.ecommerce.kafka;

import com.petproject.ecommerce.config.PropertiesReader;
import com.petproject.ecommerce.kafka.event.ProductCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ProductKafkaTestConsumer {

    private final KafkaConsumer<String, ProductCreatedEvent> consumer;


    public ProductKafkaTestConsumer() {

        Properties config = new Properties();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                PropertiesReader.get("kafka.bootstrap.servers")
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "product-service-test-group"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JacksonJsonDeserializer.class
        );


        consumer = new KafkaConsumer<>(
                config,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(ProductCreatedEvent.class)
        );


        consumer.subscribe(
                Collections.singletonList("product-events")
        );

    }

    public ConsumerRecord<String, ProductCreatedEvent> read(Long key) {

        ConsumerRecords<String, ProductCreatedEvent> records =
                consumer.poll(Duration.ofSeconds(10));


        if (records.isEmpty()) {
            throw new RuntimeException(
                    "No Kafka messages were received"
            );
        }

        String expectedKey = String.valueOf(key);

        for (ConsumerRecord<String, ProductCreatedEvent> record : records) {

            if (record.key().equals(expectedKey)) {
                return record;
            }
        }

        throw new RuntimeException(
                "Kafka message with key [" + key + "] was not found"
        );

    }


    public void close() {
        consumer.close();
    }
}