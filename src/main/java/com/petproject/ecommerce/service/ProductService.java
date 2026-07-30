package com.petproject.ecommerce.service;

import com.petproject.ecommerce.dto.request.ProductCreateRequest;
import com.petproject.ecommerce.dto.response.ProductResponse;
import com.petproject.ecommerce.exception.ProductNotFoundException;
import com.petproject.ecommerce.entity.Product;
import com.petproject.ecommerce.repository.ProductRepository;
import jakarta.persistence.Id;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
        product = productRepository.save(product);

        return new ProductResponse(product.getId(), product.getTitle(), product.getPrice());
    }
}