package com.maxxenergy.edap.controller;

import com.maxxenergy.edap.model.User;
import com.maxxenergy.edap.service.UserService;
import com.maxxenergy.edap.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Enhanced UserController with comprehensive user management features.
 * Handles user profiles, preferences, and administrative functions.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String SESSION_COOKIE = "edap_session";

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    /**
     * Redirect /user to /profile for better UX
     */
    @GetMapping
    public ResponseEntity<String> redirectToProfile() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/profile")
                .body("");
    }

    /**
     * Get user statistics API endpoint
     */
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserStats(HttpServletRequest request) {
        String email = getAuthenticatedEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        try {
            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("userId", user.getId());
            stats.put("memberSince", user.getCreatedAt());
            stats.put("lastLogin", user.getLastLogin());
            stats.put("profileComplete", user.isProfileComplete());
            stats.put("emailVerified", user.isEmailVerified());
            stats.put("role", user.getRole());
            stats.put("displayName", user.getDisplayName());
            stats.put("initials", user.getInitials());

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            logger.error("Error getting user stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to fetch user statistics"));
        }
    }

    /**
     * Update user preferences
     */
    @PostMapping("/api/preferences")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePreferences(
            @RequestBody Map<String, Object> preferences,
            HttpServletRequest request) {

        String email = getAuthenticatedEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Authentication required"));
        }

        try {
            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "User not found"));
            }

            // Update preferences
            boolean emailNotifications = Boolean.parseBoolean(
                    preferences.getOrDefault("emailNotifications", "true").toString());
            boolean darkMode = Boolean.parseBoolean(
                    preferences.getOrDefault("darkMode", "true").toString());
            String timezone = preferences.getOrDefault("timezone", "UTC").toString();
            String language = preferences.getOrDefault("language", "en").toString();

            user.updatePreferences(emailNotifications, darkMode, timezone, language);
            userService.updateUser(user);

            logger.info("Updated preferences for user: {}", email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Preferences updated successfully");
            response.put("preferences", Map.of(
                    "emailNotifications", user.isEmailNotifications(),
                    "darkMode", user.isDarkMode(),
                    "timezone", user.getTimezone(),
                    "language", user.getLanguage()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating preferences: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Unable to update preferences"));
        }
    }

    /**
     * Get user preferences
     */
    @GetMapping("/api/preferences")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPreferences(HttpServletRequest request) {
        String email = getAuthenticatedEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        try {
            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            Map<String, Object> preferences = new HashMap<>();
            preferences.put("emailNotifications", user.isEmailNotifications());
            preferences.put("darkMode", user.isDarkMode());
            preferences.put("timezone", user.getTimezone());
            preferences.put("language", user.getLanguage());

            return ResponseEntity.ok(preferences);

        } catch (Exception e) {
            logger.error("Error getting preferences: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to fetch preferences"));
        }
    }

    /**
     * Administrative endpoint to get all users (restricted to managers and above)
     */
    @GetMapping("/api/admin/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllUsers(HttpServletRequest request) {
        String email = getAuthenticatedEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        try {
            User user = userService.findByEmail(email).orElse(null);
            if (user == null || !user.hasRoleOrHigher("manager")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions"));
            }

            // This would need to be implemented in UserService
            // For now, return basic info
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User management functionality available to managers+");
            response.put("userRole", user.getRole());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error in admin endpoint: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error"));
        }
    }

    /**
     * Deactivate current user account
     */
    @PostMapping("/api/deactivate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deactivateAccount(
            @RequestParam String password,
            HttpServletRequest request) {

        String email = getAuthenticatedEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Authentication required"));
        }

        try {
            // Verify password before deactivation
            User user = userService.authenticateUser(email, password);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "error", "Invalid password"));
            }

            userService.deactivateUser(email);

            // Invalidate all sessions for this user
            // This would require session management improvements

            logger.info("User account deactivated: {}", email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Account deactivated successfully"
            ));

        } catch (Exception e) {
            logger.error("Error deactivating account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Unable to deactivate account"));
        }
    }

    // ===== UTILITY METHODS =====

    private String getAuthenticatedEmail(HttpServletRequest request) {
        String sessionToken = getSessionToken(request);
        return sessionToken != null ? sessionService.getEmailFromSession(sessionToken) : null;
    }

    private String getSessionToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (SESSION_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}