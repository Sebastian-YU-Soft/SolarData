package com.maxxenergy.edap.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Enhanced User entity for authentication, authorization, and profile management.
 * Stores user credentials, role-based permissions, and profile information.
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    @Indexed(unique = true)
    private String email;

    @JsonIgnore // Never expose password hash in JSON responses
    @NotBlank(message = "Password hash is required")
    private String passwordHash;

    private String role = "staff"; // Default role: staff, manager, director, executive

    // Enhanced profile fields
    private String department;
    private String location;
    private String phoneNumber;
    private String jobTitle;
    private String bio; // Short biography or description
    private String avatarUrl; // URL to profile picture

    // Preferences
    private boolean emailNotifications = true;
    private boolean darkMode = true;
    private String timezone = "UTC";
    private String language = "en";

    // Account status
    private boolean isActive = true;
    private boolean isEmailVerified = false;
    private boolean requiresPasswordChange = false;

    // Audit fields
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastLogin;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastPasswordChange;

    // Security tracking
    private int failedLoginAttempts = 0;
    private LocalDateTime accountLockedUntil;

    // Default constructor
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastPasswordChange = LocalDateTime.now();
    }

    /**
     * Constructor for creating new users
     * @param name Full name of the user
     * @param email Email address (will be converted to lowercase)
     * @param passwordHash Hashed password
     */
    public User(String name, String email, String passwordHash) {
        this();
        this.name = name != null ? name.trim() : null;
        this.email = email != null ? email.toLowerCase().trim() : null;
        this.passwordHash = passwordHash;
    }

    /**
     * Update profile information
     */
    public void updateProfile(String name, String department, String location,
                              String phoneNumber, String jobTitle, String bio) {
        this.name = name != null ? name.trim() : null;
        this.department = department != null && !department.trim().isEmpty() ?
                department.trim() : null;
        this.location = location != null && !location.trim().isEmpty() ?
                location.trim() : null;
        this.phoneNumber = phoneNumber != null && !phoneNumber.trim().isEmpty() ?
                phoneNumber.trim() : null;
        this.jobTitle = jobTitle != null && !jobTitle.trim().isEmpty() ?
                jobTitle.trim() : null;
        this.bio = bio != null && !bio.trim().isEmpty() ?
                bio.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update user preferences
     */
    public void updatePreferences(boolean emailNotifications, boolean darkMode,
                                  String timezone, String language) {
        this.emailNotifications = emailNotifications;
        this.darkMode = darkMode;
        this.timezone = timezone != null ? timezone : "UTC";
        this.language = language != null ? language : "en";
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase().trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.lastPasswordChange = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department != null && !department.trim().isEmpty() ?
                department.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location != null && !location.trim().isEmpty() ?
                location.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber != null && !phoneNumber.trim().isEmpty() ?
                phoneNumber.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle != null && !jobTitle.trim().isEmpty() ?
                jobTitle.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio != null && !bio.trim().isEmpty() ? bio.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        this.updatedAt = LocalDateTime.now();
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone != null ? timezone : "UTC";
        this.updatedAt = LocalDateTime.now();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language != null ? language : "en";
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isRequiresPasswordChange() {
        return requiresPasswordChange;
    }

    public void setRequiresPasswordChange(boolean requiresPasswordChange) {
        this.requiresPasswordChange = requiresPasswordChange;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getLastPasswordChange() {
        return lastPasswordChange;
    }

    public void setLastPasswordChange(LocalDateTime lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getAccountLockedUntil() {
        return accountLockedUntil;
    }

    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) {
        this.accountLockedUntil = accountLockedUntil;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update last login timestamp
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.failedLoginAttempts = 0; // Reset failed attempts on successful login
        this.accountLockedUntil = null; // Clear any account lock
    }

    /**
     * Increment failed login attempts and lock account if necessary
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        this.updatedAt = LocalDateTime.now();

        // Lock account after 5 failed attempts for 15 minutes
        if (this.failedLoginAttempts >= 5) {
            this.accountLockedUntil = LocalDateTime.now().plusMinutes(15);
        }
    }

    /**
     * Check if account is currently locked
     */
    public boolean isAccountLocked() {
        if (accountLockedUntil == null) {
            return false;
        }

        // Check if lock has expired
        if (LocalDateTime.now().isAfter(accountLockedUntil)) {
            this.accountLockedUntil = null;
            this.failedLoginAttempts = 0;
            this.updatedAt = LocalDateTime.now();
            return false;
        }

        return true;
    }

    /**
     * Check if user has specific role or higher permission level
     * @param requiredRole The minimum required role
     * @return true if user has sufficient permissions
     */
    public boolean hasRoleOrHigher(String requiredRole) {
        // Define role hierarchy: staff < manager < director < executive
        int currentLevel = getRoleLevel(this.role);
        int requiredLevel = getRoleLevel(requiredRole);
        return currentLevel >= requiredLevel;
    }

    private int getRoleLevel(String role) {
        if (role == null) return 0;
        switch (role.toLowerCase()) {
            case "executive": return 4;
            case "director": return 3;
            case "manager": return 2;
            case "staff": return 1;
            default: return 0;
        }
    }

    /**
     * Get display name for the user
     */
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        return email != null ? email.split("@")[0] : "Unknown User";
    }

    /**
     * Get user initials for avatar fallback
     */
    public String getInitials() {
        if (name == null || name.trim().isEmpty()) {
            return email != null && !email.isEmpty() ?
                    email.substring(0, 1).toUpperCase() : "?";
        }

        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
    }

    /**
     * Check if profile is complete
     */
    public boolean isProfileComplete() {
        return name != null && !name.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                department != null && !department.trim().isEmpty() &&
                location != null && !location.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", department='" + department + '\'' +
                ", location='" + location + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", isActive=" + isActive +
                ", isEmailVerified=" + isEmailVerified +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                ", failedLoginAttempts=" + failedLoginAttempts +
                '}';
    }
}