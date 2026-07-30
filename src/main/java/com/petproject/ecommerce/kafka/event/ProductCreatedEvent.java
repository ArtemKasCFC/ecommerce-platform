package com.petproject.ecommerce.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCreatedEvent {
    private Long id;
    private String title;
    private Double price;
}