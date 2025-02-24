package com.example.producttracking.controller;

import com.example.producttracking.service.BaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

public abstract class BaseController<T, ID> {
    protected final BaseService<T, ID> service;
    protected BaseController(BaseService<T, ID> service){
        this.service = service;
    }
    @GetMapping
    public List<T> getAll(){
        return service.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable ID id){
        Optional<T> entity = service.findById(id);
        return entity.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<T> create(@RequestBody T entity){
        return ResponseEntity.ok(service.save(entity));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
