package com.maxxenergy.edap.service;

import com.maxxenergy.edap.model.User;
import com.maxxenergy.edap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service class for user management operations.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    public User registerUser(String name, String email, String password) throws Exception {
        validateUserInput(name, email, password);

        String normalizedEmail = email.toLowerCase().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new Exception("Email address is already registered");
        }

        String passwordHash = hashPassword(password);
        User user = new User(name.trim(), normalizedEmail, passwordHash);
        user.setRole("staff");

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: SHA-256 algorithm not available", e);
        }
    }

    private void validateUserInput(String name, String email, String password) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Full name is required");
        }
        if (name.trim().length() < 2) {
            throw new Exception("Name must be at least 2 characters long");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email address is required");
        }
        if (!isValidEmail(email.trim())) {
            throw new Exception("Please enter a valid email address");
        }

        validatePassword(password);
    }

    private void validatePassword(String password) throws Exception {
        if (password == null || password.isEmpty()) {
            throw new Exception("Password is required");
        }
        if (password.length() < 8) {
            throw new Exception("Password must be at least 8 characters long");
        }

        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        if (!hasLetter || !hasDigit) {
            throw new Exception("Password must contain at least one letter and one number");
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}