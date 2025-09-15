package com.maxxenergy.edap.repository;

import com.maxxenergy.edap.model.SolarDataEntry;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for SolarDataEntry entities.
 * Provides thread-safe data access operations using ConcurrentHashMap.
 */
@Repository
public class InMemorySolarDataEntryRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemorySolarDataEntryRepository.class);

    private final Map<String, SolarDataEntry> entries = new ConcurrentHashMap<>();

    public SolarDataEntry save(SolarDataEntry entry) {
        if (entry.getId() == null) {
            entry = new SolarDataEntry(entry.getUserId(), entry.getPlantName(),
                    entry.getGeneration(), entry.getCapacity(), entry.getEfficiency());
            entry.setTemperature(entry.getTemperature());
            entry.setIrradiance(entry.getIrradiance());
            entry.setRevenue(entry.getRevenue());
            entry.setNotes(entry.getNotes());
            entry.setPublic(entry.isPublic());
        }

        entries.put(entry.getId(), entry);
        logger.debug("Saved solar data entry: {} for user: {}", entry.getId(), entry.getUserId());
        return entry;
    }

    public Optional<SolarDataEntry> findById(String id) {
        return Optional.ofNullable(entries.get(id));
    }

    public List<SolarDataEntry> findByUserIdOrderByTimestampDesc(String userId) {
        return entries.values().stream()
                .filter(entry -> userId.equals(entry.getUserId()))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<SolarDataEntry> findByIsPublicTrueOrderByTimestampDesc() {
        return entries.values().stream()
                .filter(SolarDataEntry::isPublic)
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<SolarDataEntry> findTop10ByUserIdOrderByTimestampDesc(String userId) {
        return findByUserIdOrderByTimestampDesc(userId).stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<SolarDataEntry> findTop10ByIsPublicTrueOrderByTimestampDesc() {
        return findByIsPublicTrueOrderByTimestampDesc().stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<SolarDataEntry> findByPlantNameContainingIgnoreCase(String plantName) {
        return entries.values().stream()
                .filter(entry -> entry.getPlantName() != null &&
                        entry.getPlantName().toLowerCase().contains(plantName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<SolarDataEntry> findByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        return entries.values().stream()
                .filter(entry -> entry.getTimestamp().isAfter(start) &&
                        entry.getTimestamp().isBefore(end))
                .collect(Collectors.toList());
    }

    public List<SolarDataEntry> findByUserIdAndTimestampBetweenOrderByTimestampDesc(
            String userId, LocalDateTime start, LocalDateTime end) {
        return entries.values().stream()
                .filter(entry -> userId.equals(entry.getUserId()))
                .filter(entry -> entry.getTimestamp().isAfter(start) &&
                        entry.getTimestamp().isBefore(end))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<SolarDataEntry> findByGenerationGreaterThanEqual(Double threshold) {
        return entries.values().stream()
                .filter(entry -> entry.getGeneration() != null &&
                        entry.getGeneration() >= threshold)
                .collect(Collectors.toList());
    }

    public List<SolarDataEntry> findByEfficiencyGreaterThanEqual(Double efficiency) {
        return entries.values().stream()
                .filter(entry -> entry.getEfficiency() != null &&
                        entry.getEfficiency() >= efficiency)
                .collect(Collectors.toList());
    }

    public List<SolarDataEntry> findGenerationByUserId(String userId) {
        return entries.values().stream()
                .filter(entry -> userId.equals(entry.getUserId()))
                .collect(Collectors.toList());
    }

    public long countByUserId(String userId) {
        return entries.values().stream()
                .filter(entry -> userId.equals(entry.getUserId()))
                .count();
    }

    public List<SolarDataEntry> findRecentEntries(LocalDateTime sinceDate) {
        return entries.values().stream()
                .filter(entry -> entry.getTimestamp().isAfter(sinceDate))
                .collect(Collectors.toList());
    }

    public void deleteByUserIdAndTimestampBefore(String userId, LocalDateTime keepAfter) {
        entries.entrySet().removeIf(entry ->
                userId.equals(entry.getValue().getUserId()) &&
                        entry.getValue().getTimestamp().isBefore(keepAfter));
        logger.debug("Cleaned up old entries for user: {}", userId);
    }

    public void deleteById(String id) {
        SolarDataEntry removed = entries.remove(id);
        if (removed != null) {
            logger.debug("Deleted solar data entry: {} for user: {}",
                    removed.getId(), removed.getUserId());
        }
    }

    public List<SolarDataEntry> findAll() {
        return new ArrayList<>(entries.values());
    }

    public long count() {
        return entries.size();
    }

    public void deleteAll() {
        entries.clear();
        logger.info("Cleared all solar data entries from repository");
    }
}