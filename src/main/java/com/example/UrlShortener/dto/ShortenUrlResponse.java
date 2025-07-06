package com.example.UrlShortener.dto;

public class ShortenUrlResponse {

    private String shortCode;
    private String shortUrl;

    // Default constructor
    public ShortenUrlResponse() {}

    // Parameterized constructor
    public ShortenUrlResponse(String shortCode, String shortUrl) {
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
    }

    // Getters and Setters
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
}
