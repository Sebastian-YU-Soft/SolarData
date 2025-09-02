package com.maxxenergy.edap.services;

import com.maxxenergy.edap.model.User;
import com.maxxenergy.edap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service class for user management operations.
 * Handles user registration, authentication, and profile management.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    /**
     * Register a new user with validation
     * @param name Full name
     * @param email Email address
     * @param password Plain text password
     * @return Created user
     * @throws Exception if validation fails or user already exists
     */
    public User registerUser(String name, String email, String password) throws Exception {
        // Validate input
        validateUserInput(name, email, password);

        String normalizedEmail = email.toLowerCase().trim();

        // Check if user already exists
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new Exception("Email address is already registered");
        }

        // Hash password
        String passwordHash = hashPassword(password);

        // Create and save user
        User user = new User(name.trim(), normalizedEmail, passwordHash);
        user.setRole("staff"); // Default role

        return userRepository.save(user);
    }

    /**
     * Authenticate user with email and password
     * @param email Email address
     * @param password Plain text password
     * @return User if authentication successful
     * @throws Exception if authentication fails
     */
    public User authenticateUser(String email, String password) throws Exception {
        if (email == null || password == null) {
            throw new Exception("Email and password are required");
        }

        Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase().trim());
        if (!userOpt.isPresent()) {
            throw new Exception("Invalid email or password");
        }

        User user = userOpt.get();
        if (!user.isActive()) {
            throw new Exception("Account is deactivated");
        }

        if (!verifyPassword(password, user.getPasswordHash())) {
            throw new Exception("Invalid email or password");
        }

        // Update last login
        user.updateLastLogin();
        userRepository.save(user);

        return user;
    }

    /**
     * Find user by email
     * @param email Email address
     * @return Optional containing user if found
     */
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    /**
     * Update user profile
     * @param userId User ID
     * @param name New name (optional)
     * @param department New department (optional)
     * @param location New location (optional)
     * @return Updated user
     * @throws Exception if user not found
     */
    public User updateProfile(String userId, String name, String department, String location)
            throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }
        if (department != null) {
            user.setDepartment(department.trim());
        }
        if (location != null) {
            user.setLocation(location.trim());
        }

        return userRepository.save(user);
    }

    /**
     * Change user password
     * @param userId User ID
     * @param oldPassword Current password
     * @param newPassword New password
     * @throws Exception if validation fails
     */
    public void changePassword(String userId, String oldPassword, String newPassword)
            throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();

        // Verify old password
        if (!verifyPassword(oldPassword, user.getPasswordHash())) {
            throw new Exception("Current password is incorrect");
        }

        // Validate new password
        validatePassword(newPassword);

        // Update password
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);
    }

    /**
     * Get users by role
     * @param role User role
     * @return List of users with the specified role
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get active users only
     * @return List of active users
     */
    public List<User> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    /**
     * Verify password against hash
     * @param password Plain text password
     * @param hash Stored password hash
     * @return true if password matches
     */
    public boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        return hashPassword(password).equals(hash);
    }

    /**
     * Hash password using SHA-256
     * @param password Plain text password
     * @return Base64 encoded hash
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
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Full name is required");
        }
        if (name.trim().length() < 2) {
            throw new Exception("Name must be at least 2 characters long");
        }
        if (name.trim().length() > 100) {
            throw new Exception("Name must be less than 100 characters");
        }

        // Validate email
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email address is required");
        }
        if (!isValidEmail(email.trim())) {
            throw new Exception("Please enter a valid email address");
        }

        // Validate password
        validatePassword(password);
    }

    /**
     * Validate password requirements
     */
    private void validatePassword(String password) throws Exception {
        if (password == null || password.isEmpty()) {
            throw new Exception("Password is required");
        }
        if (password.length() < 8) {
            throw new Exception("Password must be at least 8 characters long");
        }
        if (password.length() > 128) {
            throw new Exception("Password is too long (maximum 128 characters)");
        }

        // Additional password strength checks (optional)
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        if (!hasLetter || !hasDigit) {
            throw new Exception("Password must contain at least one letter and one number");
        }
    }

    /**
     * Validate email format
     * @param email Email address to validate
     * @return true if email is valid
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String trimmed = email.trim();

        // Basic length and character checks
        if (trimmed.length() < 6 || trimmed.length() > 254) {
            return false;
        }

        // Use regex pattern for more thorough validation
        return EMAIL_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Get user statistics
     * @return Map containing user count by role
     */
    public java.util.Map<String, Long> getUserStatistics() {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("staff", userRepository.countByRole("staff"));
        stats.put("manager", userRepository.countByRole("manager"));
        stats.put("director", userRepository.countByRole("director"));
        stats.put("executive", userRepository.countByRole("executive"));
        stats.put("total", userRepository.count());
        stats.put("active", (long) userRepository.findByIsActive(true).size());
        return stats;
    }
}