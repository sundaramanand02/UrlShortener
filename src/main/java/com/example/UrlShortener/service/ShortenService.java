package com.example.UrlShortener.service;

import com.example.UrlShortener.dto.ShortUrl;
import com.example.UrlShortener.dto.ShortenUrlRequest;
import com.example.UrlShortener.dto.ShortenUrlResponse;
import com.example.UrlShortener.dto.User;
import com.example.UrlShortener.repo.UrlRepository;
import com.example.UrlShortener.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShortenService {
    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final String BASE_URL = "http://localhost:8080/url/";

    @Transactional
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request, User user) {
        User managedUser = userService.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<ShortUrl> existingUrlOpt = urlRepository.findByOriginalUrl(request.getOriginalUrl());

        ShortUrl shortUrl;
        if (existingUrlOpt.isPresent()) {
            shortUrl = existingUrlOpt.get();
            if (!shortUrl.getUsers().contains(managedUser)) {
                shortUrl.getUsers().add(managedUser);
                managedUser.getShortUrls().add(shortUrl);
            }
        }
        else{
            String shortCode = generateShortCode();
            shortUrl = new ShortUrl();
            shortUrl.setShortCode(shortCode);
            shortUrl.setOriginalUrl(request.getOriginalUrl());
            shortUrl.setCreatedAt(LocalDateTime.now());
            managedUser.getShortUrls().add(shortUrl);
            shortUrl.getUsers().add(managedUser);
            urlRepository.save(shortUrl);
        }
        userRepository.save(managedUser);
        redisTemplate.opsForValue().set(shortUrl.getShortCode(), request.getOriginalUrl(), Duration.ofDays(5));
        return new ShortenUrlResponse(shortUrl.getShortCode(), BASE_URL + shortUrl.getShortCode());
    }

    public String resolveUrl(String code) {
        String url = redisTemplate.opsForValue().get(code);
        if (url != null) {
            redisTemplate.expire(code, Duration.ofDays(5)); // reset TTL
            return url;
        }

        Optional<ShortUrl> result = urlRepository.findById(code);
        if (result.isEmpty()) {
            throw new RuntimeException("Short code not found: " + code);
        }

        redisTemplate.opsForValue().set(code, result.get().getOriginalUrl(), Duration.ofDays(5)); // cache it
        return result.get().getOriginalUrl();
    }

    private String generateShortCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
