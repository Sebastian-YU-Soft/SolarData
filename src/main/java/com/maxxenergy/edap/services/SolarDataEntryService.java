package com.maxxenergy.edap.service;

import com.maxxenergy.edap.model.SolarDataEntry;
import com.maxxenergy.edap.repository.SolarDataEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SolarDataEntryService {

    @Autowired
    private SolarDataEntryRepository repository;

    public SolarDataEntry saveDataEntry(SolarDataEntry entry) {
        validateEntry(entry);
        return repository.save(entry);
    }

    public List<SolarDataEntry> getUserEntries(String userId) {
        return repository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<SolarDataEntry> getRecentUserEntries(String userId, int limit) {
        return repository.findTop10ByUserIdOrderByTimestampDesc(userId);
    }

    private void validateEntry(SolarDataEntry entry) {
        if (entry.getUserId() == null || entry.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (entry.getPlantName() == null || entry.getPlantName().trim().isEmpty()) {
            throw new IllegalArgumentException("Plant name is required");
        }
        if (entry.getGeneration() < 0) {
            throw new IllegalArgumentException("Generation cannot be negative");
        }
        if (entry.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (entry.getEfficiency() < 0 || entry.getEfficiency() > 100) {
            throw new IllegalArgumentException("Efficiency must be between 0 and 100");
        }
    }
}