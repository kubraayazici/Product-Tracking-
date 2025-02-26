package com.example.producttracking.service;

import com.example.producttracking.model.User;
import com.example.producttracking.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // Token oluşturmak için kullanılacak gizli anahtar (production ortamında güvenli bir şekilde saklanmalı)
    private final SecretKey jwtSecret;

    // Token'ın geçerlilik süresi (1 saat)
    private final long jwtExpirationMs = 3600000;
    public AuthService(UserRepository repository, SecretKey jwtSecret){
        this.userRepository = repository;
        this.jwtSecret = jwtSecret;
    }

    public String login(String email, String rawPassword){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(passwordEncoder.matches(rawPassword, user.getPassword())){
                user.setIsActive(true);
                userRepository.save(user);
                return generateJwtToken(user);
            }
        }
        throw new RuntimeException("Invalid credentials.");
    }

    private String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(jwtSecret, SignatureAlgorithm.HS512)
                .compact();
    }

    public void logout(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setIsActive(false);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found.");
        }
    }

}
