package com.example.UrlShortener.controllers;

import com.example.UrlShortener.dto.ShortenUrlRequest;
import com.example.UrlShortener.dto.ShortenUrlResponse;
import com.example.UrlShortener.dto.User;
import com.example.UrlShortener.service.ShortenService;
import com.example.UrlShortener.service.UserService;
import com.example.UrlShortener.util.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/url")
public class UrlShortenerController {

    @Autowired
    private UserService userService;

    @Autowired
    private final ShortenService urlShortenerService;

    public UrlShortenerController(ShortenService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @GetMapping
    public String home() {
        return "Spring Boot is working!";
    }

    @PostMapping("/saveUrl")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody ShortenUrlRequest request){
        User user = UserContextUtil.getCurrentUser();
        System.out.println("------------------------------------" + user + "------------------------------------");
        ShortenUrlResponse response = urlShortenerService.shortenUrl(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = urlShortenerService.resolveUrl(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
