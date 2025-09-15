package com.maxxenergy.edap.service;

import com.maxxenergy.edap.model.User;
import com.maxxenergy.edap.repository.InMemoryUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Enhanced service class for user management operations including authentication.
 * Uses in-memory storage instead of MongoDB.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private InMemoryUserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    /**
     * Register a new user with validation
     */
    public User registerUser(String name, String email, String password) throws Exception {
        validateUserInput(name, email, password);
        String normalizedEmail = email.toLowerCase().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new Exception("Email address is already registered");
        }

        String passwordHash = hashPassword(password);
        User user = new User(name.trim(), normalizedEmail, passwordHash);
        user.setRole("staff");

        User savedUser = userRepository.save(user);
        logger.info("New user registered: {} ({})", savedUser.getName(), savedUser.getEmail());

        return savedUser;
    }

    /**
     * Authenticate user with email and password
     */
    public User authenticateUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            logger.warn("Authentication attempt with missing credentials");
            return null;
        }

        String normalizedEmail = email.toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);

        if (!userOpt.isPresent()) {
            logger.warn("Authentication failed: user not found for email: {}", normalizedEmail);
            return null;
        }

        User user = userOpt.get();
        if (!user.isActive()) {
            logger.warn("Authentication failed: user account is inactive: {}", normalizedEmail);
            return null;
        }

        if (user.isAccountLocked()) {
            logger.warn("Authentication failed: user account is locked: {}", normalizedEmail);
            return null;
        }

        String providedPasswordHash = hashPassword(password);
        if (!providedPasswordHash.equals(user.getPasswordHash())) {
            logger.warn("Authentication failed: invalid password for user: {}", normalizedEmail);
            user.incrementFailedLoginAttempts();
            userRepository.save(user);
            return null;
        }

        logger.info("User authenticated successfully: {}", normalizedEmail);
        return user;
    }

    /**
     * Reset user password
     */
    public void resetPassword(String email, String newPassword) throws Exception {
        validatePassword(newPassword);

        String normalizedEmail = email.toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);

        if (!userOpt.isPresent()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();
        String newPasswordHash = hashPassword(newPassword);
        user.setPasswordHash(newPasswordHash);

        userRepository.save(user);
        logger.info("Password reset successfully for user: {}", normalizedEmail);
    }

    /**
     * Update user information
     */
    public User updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User and user ID cannot be null");
        }

        User updatedUser = userRepository.save(user);
        logger.debug("Updated user: {}", updatedUser.getEmail());
        return updatedUser;
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    /**
     * Check if user has required role or higher
     */
    public boolean userHasRole(String email, String requiredRole) {
        Optional<User> userOpt = findByEmail(email);
        if (!userOpt.isPresent()) {
            return false;
        }

        User user = userOpt.get();
        return user.isActive() && user.hasRoleOrHigher(requiredRole);
    }

    /**
     * Deactivate user account
     */
    public void deactivateUser(String email) throws Exception {
        Optional<User> userOpt = findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();
        user.setActive(false);
        userRepository.save(user);

        logger.info("Deactivated user account: {}", email);
    }

    /**
     * Activate user account
     */
    public void activateUser(String email) throws Exception {
        Optional<User> userOpt = findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();
        user.setActive(true);
        userRepository.save(user);

        logger.info("Activated user account: {}", email);
    }

    /**
     * Update user role
     */
    public void updateUserRole(String email, String newRole) throws Exception {
        Optional<User> userOpt = findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new Exception("User not found");
        }

        // Validate role
        if (!isValidRole(newRole)) {
            throw new Exception("Invalid role: " + newRole);
        }

        User user = userOpt.get();
        user.setRole(newRole);
        userRepository.save(user);

        logger.info("Updated role for user {} to: {}", email, newRole);
    }

    /**
     * Hash password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: SHA-256 algorithm not available", e);
        }
    }

    /**
     * Validate user input for registration
     */
    private void validateUserInput(String name, String email, String password) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Full name is required");
        }
        if (name.trim().length() < 2) {
            throw new Exception("Name must be at least 2 characters long");
        }
        if (name.trim().length() > 100) {
            throw new Exception("Name cannot exceed 100 characters");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email address is required");
        }
        if (!isValidEmail(email.trim())) {
            throw new Exception("Please enter a valid email address");
        }

        validatePassword(password);
    }

    /**
     * Validate password strength
     */
    private void validatePassword(String password) throws Exception {
        if (password == null || password.isEmpty()) {
            throw new Exception("Password is required");
        }
        if (password.length() < 8) {
            throw new Exception("Password must be at least 8 characters long");
        }

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^A-Za-z\\d].*");

        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new Exception("Password must contain uppercase, lowercase, number, and special character");
        }
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Check if role is valid
     */
    private boolean isValidRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }
        String normalizedRole = role.toLowerCase().trim();
        return normalizedRole.equals("staff") ||
                normalizedRole.equals("manager") ||
                normalizedRole.equals("director") ||
                normalizedRole.equals("executive");
    }
}