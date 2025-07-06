package com.example.UrlShortener.dto;

public class UrlHistoryResponse {

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    private String shortCode;
    private String shortUrl;
    private String originalUrl;

    public UrlHistoryResponse(String shortCode, String baseUrl, String originalUrl) {
        this.shortCode = shortCode;
        this.shortUrl = baseUrl + shortCode;
        this.originalUrl = originalUrl;
    }

    // Getters and Setters
}
