package com.example.UrlShortener.service;

import com.example.UrlShortener.dto.*;
import com.example.UrlShortener.repo.UserRepository;
import com.example.UrlShortener.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String BASE_URL;

    public User registerUser(String username, String rawPassword, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    public LoginResponse loginUser(User request){
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authManager.authenticate(authToken);

        String accessToken = JwtUtil.generateAccessToken(request.getUsername());
        String refreshToken = JwtUtil.generateRefreshToken(request.getUsername());
        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse refresh(RefreshTokenRequest request){
        if (!JwtUtil.isTokenValid(request.getRefreshToken(), "refresh")) {
            return null;
        }

        String username = JwtUtil.extractUsername(request.getRefreshToken());
        String newAccessToken = JwtUtil.generateAccessToken(username);
        return new LoginResponse(newAccessToken, request.getRefreshToken());
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UrlHistoryResponse> getUserHistory(User user) {
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return managedUser.getShortUrls().stream()
                .map(url -> new UrlHistoryResponse(
                        url.getShortCode(),
                        BASE_URL + url.getShortCode(),
                        url.getOriginalUrl()))
                .collect(Collectors.toList());
    }
}
