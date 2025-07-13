package com.example.UrlShortener.service;

import com.example.UrlShortener.dto.*;
import com.example.UrlShortener.repo.UserRepository;
import com.example.UrlShortener.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public LoginResponse loginUser(User request, HttpServletResponse response){
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authManager.authenticate(authToken);

        String accessToken = JwtUtil.generateAccessToken(request.getUsername());
        // Set refresh token in HttpOnly cookie
        addRefreshTokenToCookies(request.getUsername(), response);
        return new LoginResponse(accessToken);
    }

    public LoginResponse refresh(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        // If refresh token is missing
        if (refreshToken == null || !JwtUtil.isTokenValid(refreshToken, "refresh")) {
            return null;
        }
        String username = JwtUtil.extractUsername(refreshToken);
        updateRefreshToken(username, refreshToken, response);
        String newAccessToken = JwtUtil.generateAccessToken(username);
        return new LoginResponse(newAccessToken);
    }

    public void updateRefreshToken(String username, String refreshToken, HttpServletResponse response){
        Date expiration = JwtUtil.extractExpiration(refreshToken);

        // 🔍 Check if token expires within next 6 hours
        long timeLeftMs = expiration.getTime() - System.currentTimeMillis();
        long sixHoursInMs = 6 * 60 * 60 * 1000;
        if (timeLeftMs < sixHoursInMs) {
            // 🔄 Rotate refresh token
            addRefreshTokenToCookies(username, response);
        }
    }

    public void addRefreshTokenToCookies(String username, HttpServletResponse response){
        String refreshToken = JwtUtil.generateRefreshToken(username);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
//        refreshCookie.setSecure(true); // Set to true in production (https)
        refreshCookie.setPath("/"); // Available for all endpoints
        refreshCookie.setAttribute("SameSite", "Strict");
        refreshCookie.setMaxAge((int) (JwtUtil.getRefreshTokenValidity() / 1000));
        response.addCookie(refreshCookie);
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
