package com.maxxenergy.edap.repository;

import com.maxxenergy.edap.model.User;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for User entities.
 * Provides thread-safe data access operations using ConcurrentHashMap.
 */
@Repository
public class InMemoryUserRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryUserRepository.class);

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> emailIndex = new ConcurrentHashMap<>();

    public User save(User user) {
        if (user.getId() == null) {
            // Create new user with generated ID
            User newUser = new User(user.getName(), user.getEmail(), user.getPasswordHash());
            newUser.setRole(user.getRole());
            newUser.setDepartment(user.getDepartment());
            newUser.setLocation(user.getLocation());
            user = newUser;
        }
        user.setUpdatedAt(LocalDateTime.now());

        users.put(user.getId(), user);
        emailIndex.put(user.getEmail().toLowerCase(), user);

        logger.debug("Saved user: {} ({})", user.getName(), user.getEmail());
        return user;
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(emailIndex.get(email.toLowerCase()));
    }

    public boolean existsByEmail(String email) {
        return emailIndex.containsKey(email.toLowerCase());
    }

    public List<User> findByRole(String role) {
        return users.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .collect(Collectors.toList());
    }

    public List<User> findByIsActive(boolean isActive) {
        return users.values().stream()
                .filter(user -> user.isActive() == isActive)
                .collect(Collectors.toList());
    }

    public List<User> findByDepartment(String department) {
        return users.values().stream()
                .filter(user -> department.equals(user.getDepartment()))
                .collect(Collectors.toList());
    }

    public List<User> findByCreatedAtAfter(LocalDateTime date) {
        return users.values().stream()
                .filter(user -> user.getCreatedAt().isAfter(date))
                .collect(Collectors.toList());
    }

    public List<User> findUsersWithOldLastLogin(LocalDateTime date) {
        return users.values().stream()
                .filter(user -> user.getLastLogin() != null && user.getLastLogin().isBefore(date))
                .collect(Collectors.toList());
    }

    public long countByRole(String role) {
        return users.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .count();
    }

    public List<User> findByNameContainingIgnoreCase(String name) {
        return users.values().stream()
                .filter(user -> user.getName() != null &&
                        user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        User user = users.remove(id);
        if (user != null) {
            emailIndex.remove(user.getEmail().toLowerCase());
            logger.debug("Deleted user: {} ({})", user.getName(), user.getEmail());
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public long count() {
        return users.size();
    }

    public void deleteAll() {
        users.clear();
        emailIndex.clear();
        logger.info("Cleared all users from repository");
    }
}