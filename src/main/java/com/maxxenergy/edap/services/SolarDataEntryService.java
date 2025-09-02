package com.maxxenergy.edap.services;

import com.maxxenergy.edap.model.SolarDataEntry;
import com.maxxenergy.edap.repository.SolarDataEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for solar data entry operations.
 * Handles data validation, saving, and retrieval.
 */
@Service
public class SolarDataEntryService {

    @Autowired
    private SolarDataEntryRepository repository;

    /**
     * Save a new solar data entry with validation
     * @param entry Solar data entry to save
     * @return Saved entry with generated ID
     * @throws IllegalArgumentException if validation fails
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
     * @param userId User ID to filter by
     * @return List of user's data entries, ordered by timestamp descending
     */
    public List<SolarDataEntry> getUserEntries(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return repository.findByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * Get recent data entries for a user (limited to specified count)
     * @param userId User ID to filter by
     * @param limit Maximum number of entries to return (default 10)
     * @return List of recent user entries
     */
    public List<SolarDataEntry> getRecentUserEntries(String userId, int limit) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        // Repository method already limits to 10, but we can extend this
        return repository.findTop10ByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * Get public data entries (visible to all users)
     * @return List of public data entries
     */
    public List<SolarDataEntry> getPublicEntries() {
        return repository.findByIsPublicTrueOrderByTimestampDesc();
    }

    /**
     * Get recent public data entries
     * @return List of recent public entries (up to 10)
     */
    public List<SolarDataEntry> getRecentPublicEntries() {
        return repository.findTop10ByIsPublicTrueOrderByTimestampDesc();
    }

    /**
     * Get data entries within a date range
     * @param userId User ID (null for all users)
     * @param startDate Start date
     * @param endDate End date
     * @return List of entries within date range
     */
    public List<SolarDataEntry> getEntriesByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (userId != null && !userId.trim().isEmpty()) {
            return repository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(userId, startDate, endDate);
        } else {
            return repository.findByTimestampBetween(startDate, endDate);
        }
    }

    /**
     * Search entries by plant name
     * @param plantName Plant name to search for (partial match, case insensitive)
     * @return List of matching entries
     */
    public List<SolarDataEntry> searchByPlantName(String plantName) {
        if (plantName == null || plantName.trim().isEmpty()) {
            throw new IllegalArgumentException("Plant name cannot be null or empty");
        }
        return repository.findByPlantNameContainingIgnoreCase(plantName.trim());
    }

    /**
     * Get high-performance entries (above specified generation threshold)
     * @param generationThreshold Minimum generation in MW
     * @return List of high-performance entries
     */
    public List<SolarDataEntry> getHighPerformanceEntries(Double generationThreshold) {
        if (generationThreshold == null || generationThreshold < 0) {
            throw new IllegalArgumentException("Generation threshold must be non-negative");
        }
        return repository.findByGenerationGreaterThanEqual(generationThreshold);
    }

    /**
     * Get high-efficiency entries
     * @param efficiencyThreshold Minimum efficiency percentage
     * @return List of high-efficiency entries
     */
    public List<SolarDataEntry> getHighEfficiencyEntries(Double efficiencyThreshold) {
        if (efficiencyThreshold == null || efficiencyThreshold < 0 || efficiencyThreshold > 100) {
            throw new IllegalArgumentException("Efficiency threshold must be between 0 and 100");
        }
        return repository.findByEfficiencyGreaterThanEqual(efficiencyThreshold);
    }

    /**
     * Get user's entry count and statistics
     * @param userId User ID
     * @return Statistics about user's entries
     */
    public java.util.Map<String, Object> getUserStatistics(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        List<SolarDataEntry> entries = repository.findByUserIdOrderByTimestampDesc(userId);
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

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
     * Update an existing data entry
     * @param entryId Entry ID
     * @param updatedEntry Updated entry data
     * @return Updated entry
     * @throws Exception if entry not found or validation fails
     */
    public SolarDataEntry updateDataEntry(String entryId, SolarDataEntry updatedEntry) throws Exception {
        Optional<SolarDataEntry> existingOpt = repository.findById(entryId);
        if (!existingOpt.isPresent()) {
            throw new Exception("Data entry not found");
        }

        SolarDataEntry existing = existingOpt.get();

        // Validate updated data
        validateEntry(updatedEntry);

        // Update fields (preserve original timestamp and user ID)
        existing.setPlantName(updatedEntry.getPlantName());
        existing.setGeneration(updatedEntry.getGeneration());
        existing.setCapacity(updatedEntry.getCapacity());
        existing.setEfficiency(updatedEntry.getEfficiency());
        existing.setTemperature(updatedEntry.getTemperature());
        existing.setIrradiance(updatedEntry.getIrradiance());
        existing.setRevenue(updatedEntry.getRevenue());
        existing.setNotes(updatedEntry.getNotes());
        existing.calculateDerivedFields();

        return repository.save(existing);
    }

    /**
     * Delete a data entry
     * @param entryId Entry ID
     * @param userId User ID (for authorization check)
     * @throws Exception if entry not found or user not authorized
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
     * @param entry Entry to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateEntry(SolarDataEntry entry) throws IllegalArgumentException {
        if (entry == null) {
            throw new IllegalArgumentException("Solar data entry cannot be null");
        }

        // User ID validation
        if (entry.getUserId() == null || entry.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }

        // Plant name validation
        if (entry.getPlantName() == null || entry.getPlantName().trim().isEmpty()) {
            throw new IllegalArgumentException("Plant name is required");
        }
        if (entry.getPlantName().trim().length() > 100) {
            throw new IllegalArgumentException("Plant name must be less than 100 characters");
        }

        // Generation validation
        if (entry.getGeneration() == null) {
            throw new IllegalArgumentException("Generation value is required");
        }
        if (entry.getGeneration() < 0) {
            throw new IllegalArgumentException("Generation cannot be negative");
        }
        if (entry.getGeneration() > 10000) { // Reasonable upper limit
            throw new IllegalArgumentException("Generation value seems unrealistic (max 10,000 MW)");
        }

        // Capacity validation
        if (entry.getCapacity() == null) {
            throw new IllegalArgumentException("Capacity value is required");
        }
        if (entry.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (entry.getCapacity() > 10000) { // Reasonable upper limit
            throw new IllegalArgumentException("Capacity value seems unrealistic (max 10,000 MW)");
        }

        // Efficiency validation
        if (entry.getEfficiency() == null) {
            throw new IllegalArgumentException("Efficiency value is required");
        }
        if (entry.getEfficiency() < 0 || entry.getEfficiency() > 100) {
            throw new IllegalArgumentException("Efficiency must be between 0 and 100 percent");
        }

        // Temperature validation (optional field)
        if (entry.getTemperature() != null) {
            if (entry.getTemperature() < -50 || entry.getTemperature() > 70) {
                throw new IllegalArgumentException("Temperature must be between -50°C and 70°C");
            }
        }

        // Irradiance validation (optional field)
        if (entry.getIrradiance() != null) {
            if (entry.getIrradiance() < 0 || entry.getIrradiance() > 1500) {
                throw new IllegalArgumentException("Solar irradiance must be between 0 and 1500 W/m²");
            }
        }

        // Revenue validation (optional field)
        if (entry.getRevenue() != null && entry.getRevenue() < 0) {
            throw new IllegalArgumentException("Revenue cannot be negative");
        }

        // Business logic validation
        if (entry.getGeneration() > entry.getCapacity() * 1.1) { // Allow 10% over-capacity
            throw new IllegalArgumentException("Generation cannot significantly exceed plant capacity");
        }
    }
}