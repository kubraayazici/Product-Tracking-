package com.example.producttracking.controller;

import com.example.producttracking.dto.LoginRequest;
import com.example.producttracking.model.User;
import com.example.producttracking.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController<User, Long>{
    private final UserService userService;
    protected UserController(UserService service) {
        super(service);
        this.userService = service;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try{
            String result = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(result);
        } catch (RuntimeException exp) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user){
        return ResponseEntity.ok(userService.updateUser(id, user));
    }
}
