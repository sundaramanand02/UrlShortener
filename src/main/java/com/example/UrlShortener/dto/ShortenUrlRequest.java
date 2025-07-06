package com.example.UrlShortener.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ShortenUrlRequest {
    private String originalUrl;

    public ShortenUrlRequest() {
    }

    public ShortenUrlRequest(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}