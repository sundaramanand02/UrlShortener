package com.example.UrlShortener.repo;

import com.example.UrlShortener.dto.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<ShortUrl, String> {
    Optional<ShortUrl> findByOriginalUrl(String originalUrl);

    // No extra methods needed for now
}
