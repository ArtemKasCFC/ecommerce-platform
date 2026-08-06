package com.petproject.ecommerce.kafka.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProductCreatedEvent.class, name = "PRODUCT_CREATED"),
        @JsonSubTypes.Type(value = ProductDeletedEvent.class, name = "PRODUCT_DELETED")
})
public abstract class ProductEvent {

    private Long id;
}
