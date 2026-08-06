package com.petproject.ecommerce.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductDeletedEvent extends ProductEvent {

    public ProductDeletedEvent(Long id) {
        super(id);
    }
}
