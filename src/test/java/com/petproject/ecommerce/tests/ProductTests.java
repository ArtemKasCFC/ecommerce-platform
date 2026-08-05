package com.petproject.ecommerce.tests;

import com.petproject.ecommerce.api.ProductApi;
import com.petproject.ecommerce.database.ProductDb;
import com.petproject.ecommerce.factories.ProductFactory;
import com.petproject.ecommerce.kafka.ProductKafkaTestConsumer;
import com.petproject.ecommerce.kafka.event.ProductCreatedEvent;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.response.ErrorResponse;
import com.petproject.ecommerce.product.dto.response.ProductResponse;
import com.petproject.ecommerce.product.entity.Product;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTests {

    private ProductKafkaTestConsumer kafkaConsumer;

    @BeforeEach
    void setUpConsumer() {
        kafkaConsumer = new ProductKafkaTestConsumer();
    }

    @AfterEach
    void closeConsumer() {
        kafkaConsumer.close();
    }

    @Test
    void shouldGetProductById() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        Product productRecord = ProductDb.findById(createdProduct.getId());

        assertThat(createdProduct)
                .usingRecursiveComparison()
                .isEqualTo(productRecord);

        ProductResponse receivedProduct = ProductApi.getProduct(createdProduct.getId(), ProductResponse.class, 200);

        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);
    }

    @Disabled("Test will be rewritten when Delete method is added")
    @Test
    void shouldNotGetNonExistentProduct() {
        Long nonExistentProductId = -10000L;
        ErrorResponse errorResponse = ProductApi.getProduct(nonExistentProductId, ErrorResponse.class, 404);
        assertThat(errorResponse.getMessage()).isEqualTo("Product with id %d not found".formatted(nonExistentProductId));
        assertThat(errorResponse.getStatus()).isEqualTo(404);
    }

    @Disabled("Default response returns when id is negative")
    @Test
    void shouldNotGetProductWithNegativeId() {
        Long negativeId = -1L;
        ErrorResponse errorResponse = ProductApi.getProduct(negativeId, ErrorResponse.class, 404);
        assertThat(errorResponse.getMessage()).isEqualTo("Product with id %d not found".formatted(negativeId));
        assertThat(errorResponse.getStatus()).isEqualTo(404);
    }

    @Test
    void shouldCreateProduct() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ConsumerRecord<String, ProductCreatedEvent> event = kafkaConsumer.read(createdProduct.getId());

        assertThat(event.value()).usingRecursiveComparison().isEqualTo(createdProduct);

        Product productRecord = ProductDb.findById(createdProduct.getId());

        assertThat(createdProduct)
                .usingRecursiveComparison()
                .isEqualTo(productRecord);

        ProductResponse receivedProduct = ProductApi.getProduct(createdProduct.getId(), ProductResponse.class, 200);

        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);

    }

    @Test
    void shouldNotCreateProductWithoutTitle() {

        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setTitle(null);
        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("title", "Title must not be blank");

    }

    @Disabled("Price validation hasn't been added yet")
    @Test()
    void shouldNotCreateProductWithoutPrice() {

        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setPrice(null);
        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
//        assertThat(errorResponse.getErrors()).containsEntry("price", "Something something");

    }
    @Test()
    void shouldNotCreateProductWithNegativePrice() {

        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setPrice(-1.0);
        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", "Price must be positive");

    }
}
