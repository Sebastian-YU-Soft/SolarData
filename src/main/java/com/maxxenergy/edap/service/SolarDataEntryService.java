package com.maxxenergy.edap.service;

import com.maxxenergy.edap.model.SolarDataEntry;
import com.maxxenergy.edap.repository.InMemorySolarDataEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Service class for solar data entry operations.
 * Uses in-memory storage instead of MongoDB.
 */
@Service
public class SolarDataEntryService {

    private static final Logger logger = LoggerFactory.getLogger(SolarDataEntryService.class);

    @Autowired
    private InMemorySolarDataEntryRepository repository;

    /**
     * Save a new solar data entry with validation
     */
    public SolarDataEntry saveDataEntry(SolarDataEntry entry) throws IllegalArgumentException {
        validateEntry(entry);

        // Set timestamp if not provided
        if (entry.getTimestamp() == null) {
            entry.setTimestamp(LocalDateTime.now());
        }

        // Calculate derived fields
        entry.calculateDerivedFields();

        return repository.save(entry);
    }

    /**
     * Get all data entries for a specific user
     */
    public List<SolarDataEntry> getUserEntries(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return repository.findByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * Get recent data entries for a user (limited to specified count)
     */
    public List<SolarDataEntry> getRecentUserEntries(String userId, int limit) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return repository.findTop10ByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * Get user's entry count and statistics
     */
    public Map<String, Object> getUserStatistics(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        List<SolarDataEntry> entries = repository.findByUserIdOrderByTimestampDesc(userId);
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalEntries", entries.size());

        if (!entries.isEmpty()) {
            double avgGeneration = entries.stream()
                    .mapToDouble(e -> e.getGeneration() != null ? e.getGeneration() : 0.0)
                    .average()
                    .orElse(0.0);

            double avgEfficiency = entries.stream()
                    .mapToDouble(e -> e.getEfficiency() != null ? e.getEfficiency() : 0.0)
                    .average()
                    .orElse(0.0);

            double maxGeneration = entries.stream()
                    .mapToDouble(e -> e.getGeneration() != null ? e.getGeneration() : 0.0)
                    .max()
                    .orElse(0.0);

            double totalRevenue = entries.stream()
                    .mapToDouble(e -> e.getRevenue() != null ? e.getRevenue() : 0.0)
                    .sum();

            stats.put("averageGeneration", Math.round(avgGeneration * 10.0) / 10.0);
            stats.put("averageEfficiency", Math.round(avgEfficiency * 10.0) / 10.0);
            stats.put("maxGeneration", maxGeneration);
            stats.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);
            stats.put("lastEntry", entries.get(0).getTimestamp());
        } else {
            stats.put("averageGeneration", 0.0);
            stats.put("averageEfficiency", 0.0);
            stats.put("maxGeneration", 0.0);
            stats.put("totalRevenue", 0.0);
            stats.put("lastEntry", null);
        }

        return stats;
    }

    /**
     * Delete a data entry
     */
    public void deleteDataEntry(String entryId, String userId) throws Exception {
        Optional<SolarDataEntry> entryOpt = repository.findById(entryId);
        if (!entryOpt.isPresent()) {
            throw new Exception("Data entry not found");
        }

        SolarDataEntry entry = entryOpt.get();
        if (!entry.getUserId().equals(userId)) {
            throw new Exception("Not authorized to delete this entry");
        }

        repository.deleteById(entryId);
    }

    /**
     * Validate solar data entry
     */
    private void validateEntry(SolarDataEntry entry) throws IllegalArgumentException {
        if (entry == null) {
            throw new IllegalArgumentException("Solar data entry cannot be null");
        }

        if (entry.getUserId() == null || entry.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (entry.getPlantName() == null || entry.getPlantName().trim().isEmpty()) {
            throw new IllegalArgumentException("Plant name is required");
        }

        if (entry.getGeneration() == null || entry.getGeneration() < 0) {
            throw new IllegalArgumentException("Generation must be non-negative");
        }

        if (entry.getCapacity() == null || entry.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        if (entry.getEfficiency() == null || entry.getEfficiency() < 0 || entry.getEfficiency() > 100) {
            throw new IllegalArgumentException("Efficiency must be between 0 and 100 percent");
        }
    }
}