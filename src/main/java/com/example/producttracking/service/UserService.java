package com.example.producttracking.service;

import com.example.producttracking.model.User;
import com.example.producttracking.repository.BaseRepository;
import com.example.producttracking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService extends BaseService<User, Long>{
    private final UserRepository userRepository;
    protected UserService(UserRepository repository) {
        super(repository);
        this.userRepository = repository;
    }
    public String login(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent() && userOptional.get().getPassword().equals(password)){
            return "Login successful.";
        } else {
            throw new RuntimeException("Invalid credentials.");
        }
    }
    public User updateUser(Long id, User updatedUser){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsActive(updatedUser.getIsActive());

        return userRepository.save(user);
    }
}
