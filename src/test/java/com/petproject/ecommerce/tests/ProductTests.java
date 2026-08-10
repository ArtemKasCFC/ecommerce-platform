package com.petproject.ecommerce.tests;

import com.petproject.ecommerce.api.ProductApi;
import com.petproject.ecommerce.constants.ValidationMessages;
import com.petproject.ecommerce.database.ProductDb;
import com.petproject.ecommerce.factories.ProductFactory;
import com.petproject.ecommerce.kafka.ProductKafkaTestConsumer;
import com.petproject.ecommerce.kafka.event.ProductCreatedEvent;
import com.petproject.ecommerce.kafka.event.ProductDeletedEvent;
import com.petproject.ecommerce.kafka.event.ProductEvent;
import com.petproject.ecommerce.kafka.event.ProductUpdatedEvent;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.request.ProductUpdateRequest;
import com.petproject.ecommerce.product.dto.response.ErrorResponse;
import com.petproject.ecommerce.product.dto.response.ProductResponse;
import com.petproject.ecommerce.product.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    void shouldCreateProduct() {
        ProductCreateRequest body = ProductFactory.defaultProduct();

        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);
        assertThat(createdProduct)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(body);

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

    @Test
    void shouldNotCreateProductWithoutPrice() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setPrice(null);

        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_REQUIRED);
    }

    @Test
    void shouldNotCreateProductWithNegativePrice() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setPrice(BigDecimal.valueOf(-1.0));

        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_MUST_BE_POSITIVE);
    }

    @Test
    void shouldNotCreateProductWithZeroPrice() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setPrice(BigDecimal.valueOf(0.0));

        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_MUST_BE_POSITIVE);
    }

    @Test
    void shouldNotCreateProductWhenPriceExceedsMaxValue() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        body.setPrice(BigDecimal.valueOf(10000.1));

        ErrorResponse errorResponse = ProductApi.createProduct(body, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_TOO_HIGH);
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

        ProductResponse receivedProduct = ProductApi.getProductById(createdProduct.getId(), ProductResponse.class, 200);
        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);
    }

    @Test
    void shouldNotGetNonExistentProduct() {
        Long nonExistentProductId = Long.MAX_VALUE;

        ErrorResponse errorResponse = ProductApi.getProductById(nonExistentProductId, ErrorResponse.class, 404);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.PRODUCT_NOT_FOUND.formatted(nonExistentProductId));
        assertThat(errorResponse.getStatus()).isEqualTo(404);
    }

    @Test
    void shouldNotGetProductWithNegativeId() {
        Long negativeId = -1L;

        ErrorResponse errorResponse = ProductApi.getProductById(negativeId, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("id", ValidationMessages.ID_MUST_BE_POSITIVE);
    }

    @Test
    void shouldNotGetProductWithZeroId() {
        Long zeroId = 0L;

        ErrorResponse errorResponse = ProductApi.getProductById(zeroId, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("id", ValidationMessages.ID_MUST_BE_POSITIVE);
    }

    @Test
    void shouldUpdateProduct() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ProductUpdateRequest updateBody = ProductFactory.defaultProductUpdate();

        ProductResponse updatedProduct = ProductApi.updateProduct(updateBody, createdProduct.getId(), ProductResponse.class, 200);

        assertThat(updatedProduct)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(updateBody);

        ProductEvent event = kafkaConsumer
                .read(createdProduct.getId(), ProductUpdatedEvent.class, ofSeconds(3))
                .orElseThrow(() -> new RuntimeException("ProductUpdatedEvent was not found"));

        assertThat(event)
                .usingRecursiveComparison()
                .isEqualTo(updatedProduct);

        Product productRecord = ProductDb.findById(updatedProduct.getId());
        assertThat(updatedProduct)
                .usingRecursiveComparison()
                .isEqualTo(productRecord);

        ProductResponse receivedProduct = ProductApi.getProductById(updatedProduct.getId(), ProductResponse.class, 200);
        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(updatedProduct);
    }

    @Test
    void shouldNotUpdateNonExistentProduct() {
        Long nonExistentProductId = Long.MAX_VALUE;
        ProductUpdateRequest updateBody = ProductFactory.defaultProductUpdate();

        ErrorResponse errorResponse = ProductApi.updateProduct(updateBody, nonExistentProductId, ErrorResponse.class, 404);
        assertThat(errorResponse.getMessage()).isEqualTo(ValidationMessages.PRODUCT_NOT_FOUND.formatted(nonExistentProductId));
        assertThat(errorResponse.getStatus()).isEqualTo(404);

        assertThat(kafkaConsumer.read(nonExistentProductId, ProductUpdatedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Test
    void shouldNotUpdateProductWithNegativeId() {
        Long negativeId = -1L;
        ProductUpdateRequest updateBody = ProductFactory.defaultProductUpdate();

        ErrorResponse errorResponse = ProductApi.updateProduct(updateBody, negativeId, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("id", ValidationMessages.ID_MUST_BE_POSITIVE);

        assertThat(kafkaConsumer.read(negativeId, ProductUpdatedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Test
    void shouldNotUpdateProductWithZeroId() {
        Long zeroId = 0L;
        ProductUpdateRequest updateBody = ProductFactory.defaultProductUpdate();

        ErrorResponse errorResponse = ProductApi.updateProduct(updateBody, zeroId, ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("id", ValidationMessages.ID_MUST_BE_POSITIVE);

        assertThat(kafkaConsumer.read(zeroId, ProductUpdatedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Test
    void shouldNotUpdateProductWithoutTitle() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ProductUpdateRequest updateBody = ProductFactory.defaultProductUpdate();
        updateBody.setTitle(null);

        ErrorResponse errorResponse = ProductApi.updateProduct(updateBody, createdProduct.getId(), ErrorResponse.class, 400);

        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("title", ValidationMessages.TITLE_REQUIRED);

        ProductResponse receivedProduct = ProductApi.getProductById(createdProduct.getId(), ProductResponse.class, 200);
        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);

        assertThat(kafkaConsumer.read(createdProduct.getId(), ProductUpdatedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Test
    void shouldNotUpdateProductWhenTitleExceedsMaxLength() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ProductUpdateRequest updateBody = ProductFactory.defaultProductUpdate();
        updateBody.setTitle("A".repeat(51));

        ErrorResponse errorResponse = ProductApi.updateProduct(updateBody, createdProduct.getId(), ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("title", ValidationMessages.TITLE_TOO_LONG);

        ProductResponse receivedProduct = ProductApi.getProductById(createdProduct.getId(), ProductResponse.class, 200);
        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);

        assertThat(kafkaConsumer.read(createdProduct.getId(), ProductUpdatedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Test
    void shouldNotUpdateProductWithoutPrice() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ProductUpdateRequest updateBody = ProductFactory.defaultProductUpdate();
        updateBody.setPrice(null);

        ErrorResponse errorResponse = ProductApi.updateProduct(updateBody, createdProduct.getId(), ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_REQUIRED);

        ProductResponse receivedProduct = ProductApi.getProductById(createdProduct.getId(), ProductResponse.class, 200);
        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);

        assertThat(kafkaConsumer.read(createdProduct.getId(), ProductUpdatedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Test
    void shouldNotUpdateProductWithNegativePrice() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ProductUpdateRequest updateBody = ProductFactory.defaultProductUpdate();
        updateBody.setPrice(BigDecimal.valueOf(-1.0));

        ErrorResponse errorResponse = ProductApi.updateProduct(updateBody, createdProduct.getId(), ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_MUST_BE_POSITIVE);

        ProductResponse receivedProduct = ProductApi.getProductById(createdProduct.getId(), ProductResponse.class, 200);
        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);

        assertThat(kafkaConsumer.read(createdProduct.getId(), ProductUpdatedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Test
    void shouldNotUpdateProductWithZeroPrice() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ProductUpdateRequest updateBody = ProductFactory.defaultProductUpdate();
        updateBody.setPrice(BigDecimal.valueOf(0.0));

        ErrorResponse errorResponse = ProductApi.updateProduct(updateBody, createdProduct.getId(), ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_MUST_BE_POSITIVE);

        ProductResponse receivedProduct = ProductApi.getProductById(createdProduct.getId(), ProductResponse.class, 200);
        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);

        assertThat(kafkaConsumer.read(createdProduct.getId(), ProductUpdatedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Test
    void shouldNotUpdateProductWhenPriceExceedsMaxValue() {
        ProductCreateRequest body = ProductFactory.defaultProduct();
        ProductResponse createdProduct = ProductApi.createProduct(body, ProductResponse.class, 201);

        ProductUpdateRequest updateBody = ProductFactory.defaultProductUpdate();
        updateBody.setPrice(BigDecimal.valueOf(10000.1));

        ErrorResponse errorResponse = ProductApi.updateProduct(updateBody, createdProduct.getId(), ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("price", ValidationMessages.PRICE_TOO_HIGH);

        ProductResponse receivedProduct = ProductApi.getProductById(createdProduct.getId(), ProductResponse.class, 200);
        assertThat(receivedProduct)
                .usingRecursiveComparison()
                .isEqualTo(createdProduct);

        assertThat(kafkaConsumer.read(createdProduct.getId(), ProductUpdatedEvent.class, ofSeconds(3))).isEmpty();
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

    @Test
    void shouldNotDeleteProductWithNegativeId() {
        Long negativeId = -1L;

        ErrorResponse errorResponse = ProductApi.deleteProductById(negativeId, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("id", ValidationMessages.ID_MUST_BE_POSITIVE);

        assertThat(kafkaConsumer.read(negativeId, ProductDeletedEvent.class, ofSeconds(3))).isEmpty();
    }

    @Test
    void shouldNotDeleteProductWithZeroId() {
        Long zeroId = 0L;

        ErrorResponse errorResponse = ProductApi.deleteProductById(zeroId, ErrorResponse.class, 400);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrors()).containsEntry("id", ValidationMessages.ID_MUST_BE_POSITIVE);

        assertThat(kafkaConsumer.read(zeroId, ProductDeletedEvent.class, ofSeconds(3))).isEmpty();
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
