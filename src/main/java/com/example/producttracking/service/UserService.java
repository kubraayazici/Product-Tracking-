package com.example.producttracking.service;

import com.example.producttracking.model.User;
import com.example.producttracking.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService extends BaseService<User, Long>{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    protected UserService(UserRepository repository, BCryptPasswordEncoder passwordEncoder) {
        super(repository);
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user){
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }
    public User updateUser(Long id, User updatedUser){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        if(updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsActive(updatedUser.getIsActive());

        return userRepository.save(user);
    }

    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
}
