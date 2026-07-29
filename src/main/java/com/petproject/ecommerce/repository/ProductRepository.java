package com.petproject.ecommerce.repository;

import com.petproject.ecommerce.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ProductRepository {

    private final List<Product> products;

    public ProductRepository() {
        this.products = new ArrayList<>();

        products.add(new Product(1L, "iPhone", 1000.0));
        products.add(new Product(2L, "MacBook", 3000.0));
        products.add(new Product(3L, "AirPods", 500.0));
    }

    public List<Product> findAll() {
        return products;
    }

    public Product findById(Long id) {
        return products.stream()
                .filter(product -> Objects.equals(product.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public Product save(Product product){
        products.add(product);
        return product;
    }
}
