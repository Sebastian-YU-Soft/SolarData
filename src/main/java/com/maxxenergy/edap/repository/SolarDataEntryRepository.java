package com.maxxenergy.edap.repository;

import com.maxxenergy.edap.model.SolarDataEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for SolarDataEntry entities.
 * Provides data access operations for solar data management.
 */
@Repository
public interface SolarDataEntryRepository extends MongoRepository<SolarDataEntry, String> {

    /**
     * Find all data entries for a specific user, ordered by timestamp descending
     * @param userId User ID to filter by
     * @return List of user's data entries, newest first
     */
    List<SolarDataEntry> findByUserIdOrderByTimestampDesc(String userId);

    /**
     * Find public data entries only, ordered by timestamp descending
     * @return List of public data entries
     */
    List<SolarDataEntry> findByIsPublicTrueOrderByTimestampDesc();

    /**
     * Find recent data entries for a user (limited count)
     * @param userId User ID to filter by
     * @return List of recent user entries (up to 10)
     */
    List<SolarDataEntry> findTop10ByUserIdOrderByTimestampDesc(String userId);

    /**
     * Find recent public data entries (limited count)
     * @return List of recent public entries (up to 10)
     */
    List<SolarDataEntry> findTop10ByIsPublicTrueOrderByTimestampDesc();

    /**
     * Find data entries by plant name (case insensitive)
     * @param plantName Plant name to search for
     * @return List of matching entries
     */
    @Query("{'plantName': {$regex: ?0, $options: 'i'}}")
    List<SolarDataEntry> findByPlantNameContainingIgnoreCase(String plantName);

    /**
     * Find data entries within a date range
     * @param start Start date
     * @param end End date
     * @return List of entries within the date range
     */
    List<SolarDataEntry> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find user's data entries within a date range
     * @param userId User ID
     * @param start Start date
     * @param end End date
     * @return List of user's entries within the date range
     */
    List<SolarDataEntry> findByUserIdAndTimestampBetweenOrderByTimestampDesc(
            String userId, LocalDateTime start, LocalDateTime end);

    /**
     * Find entries with generation above a threshold
     * @param threshold Minimum generation value
     * @return List of high-performance entries
     */
    @Query("{'generation': {$gte: ?0}}")
    List<SolarDataEntry> findByGenerationGreaterThanEqual(Double threshold);

    /**
     * Find entries with efficiency above a threshold
     * @param efficiency Minimum efficiency percentage
     * @return List of high-efficiency entries
     */
    @Query("{'efficiency': {$gte: ?0}}")
    List<SolarDataEntry> findByEfficiencyGreaterThanEqual(Double efficiency);

    /**
     * Get average generation for a user
     * @param userId User ID
     * @return Average generation value
     */
    @Query(value = "{'userId': ?0}", fields = "{'generation': 1}")
    List<SolarDataEntry> findGenerationByUserId(String userId);

    /**
     * Count entries by user
     * @param userId User ID
     * @return Number of entries for the user
     */
    long countByUserId(String userId);

    /**
     * Find entries created in the last N days
     * @param days Number of days to look back
     * @return List of recent entries
     */
    @Query("{'timestamp': {$gte: ?0}}")
    List<SolarDataEntry> findRecentEntries(LocalDateTime sinceDate);

    /**
     * Delete old entries for a user (keeping only recent ones)
     * @param userId User ID
     * @param keepAfter Date to keep entries after
     */
    void deleteByUserIdAndTimestampBefore(String userId, LocalDateTime keepAfter);
}