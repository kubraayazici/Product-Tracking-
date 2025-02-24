package com.example.producttracking.controller;

import com.example.producttracking.model.Product;
import com.example.producttracking.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController extends BaseController<Product, Long>{

    private final ProductService productService;
    protected ProductController(ProductService service) {
        super(service);
        this.productService = service;
    }
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product){
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }
}
