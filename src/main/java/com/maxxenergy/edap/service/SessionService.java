package com.maxxenergy.edap.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing user sessions and password reset tokens.
 * Handles session creation, validation, and cleanup.
 */
@Service
public class SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    // In-memory storage for demo purposes
    // In production, use Redis or database with proper expiration
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    private final Map<String, ResetTokenInfo> resetTokens = new ConcurrentHashMap<>();

    private final SecureRandom secureRandom = new SecureRandom();

    // Session timeout in seconds (8 hours)
    private static final int SESSION_TIMEOUT = 8 * 60 * 60;

    // Reset token timeout in seconds (1 hour)
    private static final int RESET_TOKEN_TIMEOUT = 60 * 60;

    /**
     * Inner class to hold session information
     */
    private static class SessionInfo {
        final String email;
        final LocalDateTime createdAt;
        final LocalDateTime expiresAt;

        SessionInfo(String email) {
            this.email = email;
            this.createdAt = LocalDateTime.now();
            this.expiresAt = this.createdAt.plusSeconds(SESSION_TIMEOUT);
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }

    /**
     * Inner class to hold password reset token information
     */
    private static class ResetTokenInfo {
        final String email;
        final LocalDateTime createdAt;
        final LocalDateTime expiresAt;

        ResetTokenInfo(String email) {
            this.email = email;
            this.createdAt = LocalDateTime.now();
            this.expiresAt = this.createdAt.plusSeconds(RESET_TOKEN_TIMEOUT);
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }

    /**
     * Create a new session for the user
     */
    public String createSession(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        String sessionToken = generateSecureToken();
        sessions.put(sessionToken, new SessionInfo(email.toLowerCase().trim()));

        logger.debug("Created session for user: {}", email);
        cleanupExpiredSessions();

        return sessionToken;
    }

    /**
     * Get email from session token
     */
    public String getEmailFromSession(String sessionToken) {
        if (sessionToken == null || sessionToken.trim().isEmpty()) {
            return null;
        }

        SessionInfo sessionInfo = sessions.get(sessionToken);
        if (sessionInfo == null) {
            return null;
        }

        if (sessionInfo.isExpired()) {
            sessions.remove(sessionToken);
            logger.debug("Removed expired session");
            return null;
        }

        return sessionInfo.email;
    }

    /**
     * Invalidate a session
     */
    public void invalidateSession(String sessionToken) {
        if (sessionToken != null) {
            SessionInfo removed = sessions.remove(sessionToken);
            if (removed != null) {
                logger.debug("Invalidated session for user: {}", removed.email);
            }
        }
    }

    /**
     * Create a password reset token
     */
    public String createPasswordResetToken(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        String resetToken = generateSecureToken();
        resetTokens.put(resetToken, new ResetTokenInfo(email.toLowerCase().trim()));

        logger.debug("Created password reset token for user: {}", email);
        cleanupExpiredResetTokens();

        return resetToken;
    }

    /**
     * Check if reset token is valid
     */
    public boolean isValidResetToken(String resetToken) {
        if (resetToken == null || resetToken.trim().isEmpty()) {
            return false;
        }

        ResetTokenInfo tokenInfo = resetTokens.get(resetToken);
        if (tokenInfo == null) {
            return false;
        }

        if (tokenInfo.isExpired()) {
            resetTokens.remove(resetToken);
            logger.debug("Removed expired reset token");
            return false;
        }

        return true;
    }

    /**
     * Get email from reset token
     */
    public String getEmailFromResetToken(String resetToken) {
        if (!isValidResetToken(resetToken)) {
            return null;
        }

        ResetTokenInfo tokenInfo = resetTokens.get(resetToken);
        return tokenInfo != null ? tokenInfo.email : null;
    }

    /**
     * Invalidate a reset token
     */
    public void invalidateResetToken(String resetToken) {
        if (resetToken != null) {
            ResetTokenInfo removed = resetTokens.remove(resetToken);
            if (removed != null) {
                logger.debug("Invalidated reset token for user: {}", removed.email);
            }
        }
    }

    /**
     * Generate a secure random token
     */
    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Clean up expired sessions
     */
    private void cleanupExpiredSessions() {
        sessions.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                logger.debug("Cleaned up expired session for user: {}", entry.getValue().email);
                return true;
            }
            return false;
        });
    }

    /**
     * Clean up expired reset tokens
     */
    private void cleanupExpiredResetTokens() {
        resetTokens.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                logger.debug("Cleaned up expired reset token for user: {}", entry.getValue().email);
                return true;
            }
            return false;
        });
    }

    /**
     * Get session statistics for monitoring
     */
    public Map<String, Object> getSessionStatistics() {
        cleanupExpiredSessions();
        cleanupExpiredResetTokens();

        return Map.of(
                "activeSessions", sessions.size(),
                "pendingResetTokens", resetTokens.size(),
                "sessionTimeout", SESSION_TIMEOUT,
                "resetTokenTimeout", RESET_TOKEN_TIMEOUT
        );
    }
}