package com.example.producttracking.repository;

import com.example.producttracking.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long>{
    Optional<User> findByEmail(String email);
}
