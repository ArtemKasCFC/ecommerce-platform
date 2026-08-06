package com.petproject.ecommerce.kafka;

import com.petproject.ecommerce.config.PropertiesReader;
import com.petproject.ecommerce.kafka.event.ProductEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

public class ProductKafkaTestConsumer {

    private final KafkaConsumer<String, ProductEvent> consumer;


    public ProductKafkaTestConsumer() {

        Properties config = new Properties();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, PropertiesReader.get("kafka.bootstrap.servers"));

        config.put(ConsumerConfig.GROUP_ID_CONFIG, "product-service-test-group");

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);


        consumer = new KafkaConsumer<>(config, new StringDeserializer(), new JacksonJsonDeserializer<>(ProductEvent.class));


        consumer.subscribe(Collections.singletonList("product-events"));

        consumer.poll(Duration.ofSeconds(1));

    }

    public Optional<ProductEvent> read(Long key, Class<? extends ProductEvent> eventType, Duration timeout) {
        String expectedKey = String.valueOf(key);

        long endTime = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < endTime) {

            ConsumerRecords<String, ProductEvent> records = consumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, ProductEvent> record : records) {

                if (record.key().equals(expectedKey) && eventType.isInstance(record.value())) {
                    return Optional.of(record.value());
                }
            }
        }

        return Optional.empty();
    }


    public void close() {
        consumer.close();
    }
}