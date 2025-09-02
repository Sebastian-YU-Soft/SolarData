package com.maxxenergy.edap.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * User entity for authentication and authorization.
 * Stores user credentials and role-based permissions.
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

    private String department;

    private String location;

    private boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime lastLogin;

    // Default constructor
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
        this.department = department;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    /**
     * Update last login timestamp
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", department='" + department + '\'' +
                ", location='" + location + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                '}';
    }
}