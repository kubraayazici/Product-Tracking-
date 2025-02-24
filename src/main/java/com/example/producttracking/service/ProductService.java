package com.example.producttracking.service;

import com.example.producttracking.model.Product;
import com.example.producttracking.repository.BaseRepository;
import com.example.producttracking.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductService extends BaseService<Product, Long>{

    private final ProductRepository productRepository;
    protected ProductService(ProductRepository repository) {
        super(repository);
        this.productRepository = repository;
    }

    public Product updateProduct(Long id, Product product) {
        Product updatedProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        updatedProduct.setName(product.getName());
        updatedProduct.setType(product.getType());
        updatedProduct.setQuantity(product.getQuantity());
        updatedProduct.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(updatedProduct);
    }
}
