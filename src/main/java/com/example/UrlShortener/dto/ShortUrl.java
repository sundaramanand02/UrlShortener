package com.example.UrlShortener.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "short_urls")
@Data
public class ShortUrl {

    @Id
    private String shortCode;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    private LocalDateTime createdAt;

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @ManyToMany(mappedBy = "shortUrls")
    private Set<User> users = new HashSet<>();

    // Constructors
    public ShortUrl() {}

    public ShortUrl(String shortCode, String originalUrl, LocalDateTime createdAt) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

/*
1. any user can get any url
2. for any specific user, history page
3. no edits, delete allowed
*/