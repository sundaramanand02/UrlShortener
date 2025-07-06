package com.example.UrlShortener.util;

import com.example.UrlShortener.dto.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContextUtil {
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            System.out.println("---------------------------------------"+ principal+"---------------------------------------");
            if (principal instanceof User) {
                return ((User) principal);
            }
        }
        return null;
    }
}
