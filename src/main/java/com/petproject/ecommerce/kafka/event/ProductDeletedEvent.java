package com.petproject.ecommerce.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString(callSuper = true)
public class ProductDeletedEvent extends ProductEvent {

    public ProductDeletedEvent(Long id) {
        super(id);
    }

    @Override
    public String getEventType() {
        return "PRODUCT_DELETED";
    }
}
