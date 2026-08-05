package com.petproject.ecommerce.kafka.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ToString
public class ProductCreatedEvent {
    private Long id;
    private String title;
    private BigDecimal price;
}