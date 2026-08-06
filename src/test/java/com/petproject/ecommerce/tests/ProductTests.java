package com.petproject.ecommerce.tests;

import com.petproject.ecommerce.api.ProductApi;
import com.petproject.ecommerce.constants.ValidationMessages;
import com.petproject.ecommerce.database.ProductDb;
import com.petproject.ecommerce.factories.ProductFactory;
import com.petproject.ecommerce.kafka.ProductKafkaTestConsumer;
import com.petproject.ecommerce.kafka.event.ProductCreatedEvent;
import com.petproject.ecommerce.kafka.event.ProductDeletedEvent;
import com.petproject.ecommerce.kafka.event.ProductEvent;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.response.ErrorResponse;
import com.petproject.ecommerce.product.dto.response.ProductResponse;
import com.petproject.ecommerce.product.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.time.Duration.ofSeconds;
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
    void shouldGetProducts() {
        List<ProductResponse> products = ProductApi.getProducts(200);
        assertThat(products).isNotEmpty();
        assertThat(products).allMatch(product -> product.getId() != null && product.getTitle() != null && product.getPrice() != null);
    }

    @Test
    void shouldGetProductById() {
        ProductCreateRequest body = ProductFactory.defaultProduct();

        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        Product productRecord = ProductDb.findById(createdProduct.getId());
        assertThat(createdProduct)
                .usingRecursiveComparison()
                .isEqualTo(productRecord);

        ProductResponse receivedProduct = ProductApi.getProductById(createdProduct.getId(), ProductResponse.class, 200);
        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);
    }

    @Disabled("Test will be rewritten when Delete method is added")
    @Test
    void shouldNotGetNonExistentProduct() {
        Long nonExistentProductId = Long.MAX_VALUE;

        ErrorResponse errorResponse = ProductApi.getProductById(nonExistentProductId, ErrorResponse.class, 404);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.PRODUCT_NOT_FOUND.formatted(nonExistentProductId));
        assertThat(errorResponse.getStatus()).isEqualTo(404);
    }

    @Disabled("Validation will be added later")
    @Test
    void shouldNotGetProductWithNegativeId() {
        Long negativeId = -1L;

        ErrorResponse errorResponse = ProductApi.getProductById(negativeId, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
    }

    @Test
    void shouldCreateProduct() {
        ProductCreateRequest body = ProductFactory.defaultProduct();

        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ProductEvent event = kafkaConsumer
                .read(createdProduct.getId(), ProductCreatedEvent.class, ofSeconds(3))
                .orElseThrow(() -> new RuntimeException("ProductCreatedEvent was not found"));

        assertThat(event)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);

        Product productRecord = ProductDb.findById(createdProduct.getId());
        assertThat(createdProduct)
                .usingRecursiveComparison()
                .isEqualTo(productRecord);

        ProductResponse receivedProduct = ProductApi.getProductById(createdProduct.getId(), ProductResponse.class, 200);
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
        assertThat(errorResponse.getErrors()).containsEntry("title", ValidationMessages.TITLE_REQUIRED);
    }

    @Test
    void shouldNotCreateProductWhenTitleExceedsMaxLength() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setTitle("A".repeat(51));

        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("title", ValidationMessages.TITLE_TOO_LONG);
    }

    @Test()
    void shouldNotCreateProductWithoutPrice() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setPrice(null);

        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_REQUIRED);
    }

    @Test()
    void shouldNotCreateProductWithNegativePrice() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setPrice(BigDecimal.valueOf(-1.0));

        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_MUST_BE_POSITIVE);
    }

    @Test()
    void shouldNotCreateProductWhenPriceExceedsMaxValue() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setPrice(BigDecimal.valueOf(10000.1));

        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_TOO_HIGH);
    }


    @Test
    void shouldDeleteProduct() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ProductApi.deleteProductById(createdProduct.getId(), Void.class, 204);
        assertThat(ProductDb.existsById(createdProduct.getId())).isFalse();

        ProductEvent event = kafkaConsumer
                .read(createdProduct.getId(), ProductDeletedEvent.class, ofSeconds(3))
                .orElseThrow(() -> new RuntimeException("ProductDeletedEvent was not found"));

        assertThat(event.getId()).isEqualTo(createdProduct.getId());

        ProductApi.getProductById(createdProduct.getId(), ErrorResponse.class, 404);
    }

    @Test
    void shouldNotDeleteNonExistentProduct() {
        Long nonExistentProductId = Long.MAX_VALUE;

        ErrorResponse errorResponse = ProductApi.deleteProductById(nonExistentProductId, ErrorResponse.class, 404);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.PRODUCT_NOT_FOUND.formatted(nonExistentProductId));
        assertThat(errorResponse.getStatus()).isEqualTo(404);

        assertThat(kafkaConsumer.read(nonExistentProductId, ProductDeletedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Disabled("Issue with the deserialization (add handler for 400sc responses)")
    @Test
    void shouldNotDeleteProductWithNegativeId() {
        Long nonExistentProductId = -1L;

        ProductApi.deleteProductById(nonExistentProductId, ErrorResponse.class, 400);

        assertThat(kafkaConsumer.read(nonExistentProductId, ProductDeletedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Test
    void shouldNotDeleteProductTwice() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ProductApi.deleteProductById(createdProduct.getId(), Void.class, 204);

        ProductEvent event = kafkaConsumer
                .read(createdProduct.getId(), ProductDeletedEvent.class, ofSeconds(3))
                .orElseThrow(() -> new RuntimeException("ProductDeletedEvent was not found"));

        assertThat(event.getId()).isEqualTo(createdProduct.getId());

        ErrorResponse errorResponse = ProductApi.deleteProductById(createdProduct.getId(), ErrorResponse.class, 404);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.PRODUCT_NOT_FOUND.formatted(createdProduct.getId()));
        assertThat(errorResponse.getStatus()).isEqualTo(404);

        assertThat(kafkaConsumer.read(createdProduct.getId(), ProductDeletedEvent.class, ofSeconds(3))).isEmpty();
    }
}
