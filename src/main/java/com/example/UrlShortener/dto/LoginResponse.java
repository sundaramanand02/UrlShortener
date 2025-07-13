package com.example.UrlShortener.dto;

import lombok.AllArgsConstructor;

public class LoginResponse {
    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    private String accessToken;
}
