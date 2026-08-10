package com.petproject.ecommerce.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductUpdatedEvent extends ProductEvent {

    private String title;
    private BigDecimal price;

    public ProductUpdatedEvent(Long id, String title, BigDecimal price) {
        super(id);
        this.title = title;
        this.price = price;
    }
}
