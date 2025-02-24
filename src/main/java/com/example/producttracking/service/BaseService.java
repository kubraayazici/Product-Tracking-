package com.example.producttracking.service;

import com.example.producttracking.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public abstract class BaseService<T, ID> {
    protected final BaseRepository<T, ID> repository;
    protected BaseService(BaseRepository<T, ID> repository){
        this.repository = repository;
    }
    public List<T> findAll(){
        return repository.findAll();
    }
    public Optional<T> findById(ID id){
        return repository.findById(id);
    }
    public T save(T entity){
        return repository.save(entity);
    }
    public void deleteById(ID id){
        repository.deleteById(id);
    }
}
