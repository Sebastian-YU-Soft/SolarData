package com.maxxenergy.edap.repository;

import com.maxxenergy.edap.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entities.
 * Provides data access operations for user management.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by email address (case insensitive)
     * @param email User email address
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email
     * @param email Email address to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role
     * @param role User role (staff, manager, director, executive)
     * @return List of users with the specified role
     */
    List<User> findByRole(String role);

    /**
     * Find active users only
     * @param isActive Active status
     * @return List of active users
     */
    List<User> findByIsActive(boolean isActive);

    /**
     * Find users by department
     * @param department Department name
     * @return List of users in the department
     */
    List<User> findByDepartment(String department);

    /**
     * Find users created after a specific date
     * @param date The date to filter from
     * @return List of users created after the date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find users who haven't logged in since a specific date
     * @param date The date to check against
     * @return List of users with old last login
     */
    @Query("{'lastLogin': {$lt: ?0}}")
    List<User> findUsersWithOldLastLogin(LocalDateTime date);

    /**
     * Count users by role
     * @param role User role
     * @return Number of users with the role
     */
    long countByRole(String role);

    /**
     * Find users by partial name match (case insensitive)
     * @param name Partial name to search for
     * @return List of matching users
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<User> findByNameContainingIgnoreCase(String name);
}