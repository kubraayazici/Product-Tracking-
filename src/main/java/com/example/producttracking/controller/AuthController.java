package com.example.producttracking.controller;

import com.example.producttracking.dto.LoginRequest;
import com.example.producttracking.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService){
        this.authService = authService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try{
            String result = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(Collections.singletonMap("token", result));
        } catch (RuntimeException exp) {
            System.err.println("Login failed for user " + loginRequest.getEmail() + ": " + exp.getMessage());
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LoginRequest loginRequest){
        try{
            authService.logout(loginRequest.getEmail());
            return ResponseEntity.ok("Logout successful.");
        } catch (RuntimeException exp) {
            System.err.println("Logout failed for user " + loginRequest.getEmail() + ": " + exp.getMessage());
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout failed.");
        }
    }
}
