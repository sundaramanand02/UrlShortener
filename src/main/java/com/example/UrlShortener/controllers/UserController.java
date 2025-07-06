package com.example.UrlShortener.controllers;


import com.example.UrlShortener.dto.LoginResponse;
import com.example.UrlShortener.dto.RefreshTokenRequest;
import com.example.UrlShortener.dto.UrlHistoryResponse;
import com.example.UrlShortener.dto.User;
import com.example.UrlShortener.service.UserService;
import com.example.UrlShortener.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

//    @Autowired private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        user = userService.registerUser(user.getUsername(), user.getPassword(), user.getRole());
        return ResponseEntity.ok("User registered with ID: " + user.getUserId());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<UrlHistoryResponse>> getUserUrlHistory(@RequestAttribute("user") User user) {
        List<UrlHistoryResponse> history = userService.getUserHistory(user);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        LoginResponse loginResponse = userService.refresh(request);
        return loginResponse == null ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token")
                : ResponseEntity.ok(loginResponse);
    }

}
