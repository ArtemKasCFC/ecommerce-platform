package com.petproject.ecommerce.product.service;

import com.petproject.ecommerce.exception.ProductNotFoundException;
import com.petproject.ecommerce.kafka.event.ProductCreatedEvent;
import com.petproject.ecommerce.kafka.event.ProductDeletedEvent;
import com.petproject.ecommerce.kafka.event.ProductUpdatedEvent;
import com.petproject.ecommerce.kafka.producer.ProductEventProducer;
import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.request.ProductUpdateRequest;
import com.petproject.ecommerce.product.dto.response.ProductResponse;
import com.petproject.ecommerce.product.entity.Product;
import com.petproject.ecommerce.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventProducer productEventProducer;


    public ProductService(ProductRepository productRepository, ProductEventProducer productEventProducer) {
        this.productRepository = productRepository;
        this.productEventProducer = productEventProducer;
    }

    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = new Product(request.getTitle(), request.getPrice());
        Product savedProduct = productRepository.save(product);

        productEventProducer.send(new ProductCreatedEvent(savedProduct.getId(), savedProduct.getTitle(), savedProduct.getPrice()));

        return new ProductResponse(savedProduct.getId(), savedProduct.getTitle(), savedProduct.getPrice());
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }


    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
    }

    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());

        Product updatedProduct = productRepository.save(product);

        productEventProducer.send(new ProductUpdatedEvent(updatedProduct.getId(), updatedProduct.getTitle(), updatedProduct.getPrice()));

        return new ProductResponse(updatedProduct.getId(), updatedProduct.getTitle(), updatedProduct.getPrice());
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        productRepository.delete(product);

        productEventProducer.send(new ProductDeletedEvent(product.getId()));
    }


}