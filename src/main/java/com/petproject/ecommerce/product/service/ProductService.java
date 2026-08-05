package com.petproject.ecommerce.product.service;

import com.petproject.ecommerce.product.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.product.dto.response.ProductResponse;
import com.petproject.ecommerce.exception.ProductNotFoundException;
import com.petproject.ecommerce.product.entity.Product;
import com.petproject.ecommerce.kafka.event.ProductCreatedEvent;
import com.petproject.ecommerce.kafka.producer.ProductEventProducer;
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


    public List<Product> getProducts() {
        return productRepository.findAll();
    }


    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product with id " + id + " not found"));
    }


    public ProductResponse create(ProductCreateRequest request) {
        Product product = new Product(request.getTitle(), request.getPrice());
        Product savedProduct = productRepository.save(product);

        productEventProducer.sendProductCreatedEvent(new ProductCreatedEvent(savedProduct.getId(), savedProduct.getTitle(), savedProduct.getPrice()));

        return new ProductResponse(product.getId(), product.getTitle(), product.getPrice());
    }
}