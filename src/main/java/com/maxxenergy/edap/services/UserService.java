package com.maxxenergy.edap.service;

import com.maxxenergy.edap.model.User;
import com.maxxenergy.edap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(String name, String email, String password) throws Exception {
        // Check if user already exists
        if (userRepository.existsByEmail(email.toLowerCase())) {
            throw new Exception("Email already registered");
        }

        // Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Name is required");
        }
        if (email == null || !isValidEmail(email)) {
            throw new Exception("Valid email is required");
        }
        if (password == null || password.length() < 8) {
            throw new Exception("Password must be at least 8 characters");
        }

        // Hash password
        String passwordHash = hashPassword(password);

        // Create and save user
        User user = new User(name.trim(), email.trim(), passwordHash);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    public boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        return trimmed.contains("@") && trimmed.contains(".") && trimmed.length() >= 6;
    }
}