package com.maxxenergy.edap.service;

import com.maxxenergy.edap.model.SolarData;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.HashMap;

/**
 * Service for retrieving public solar data using in-memory demo data.
 * No external database dependencies required.
 */
@Service
public class SolarDataService {

    private static final Logger logger = LoggerFactory.getLogger(SolarDataService.class);

    @PostConstruct
    public void initialize() {
        logger.info("Initializing SolarDataService with in-memory demo data");
        logger.info("Solar data will have dynamic variations to simulate real-time updates");
    }

    public SolarData getPublicData() {
        // Create dynamic demo data with realistic variations
        long currentMinute = System.currentTimeMillis() / (1000 * 60);
        double timeVariation = Math.sin(currentMinute * 0.1) * 0.3;
        double hourVariation = Math.sin((System.currentTimeMillis() / (1000 * 60 * 60)) * 0.5) * 2.0;

        double baseGeneration = 12.5;
        double baseRevenue = 156000.0;

        // Add some realistic variation based on time of day
        int hour = java.time.LocalTime.now().getHour();
        double timeOfDayFactor = 1.0;

        if (hour >= 6 && hour <= 18) {
            // Daytime - higher generation
            timeOfDayFactor = 0.8 + 0.4 * Math.sin(Math.PI * (hour - 6) / 12);
        } else {
            // Nighttime - minimal generation
            timeOfDayFactor = 0.1;
        }

        double currentGeneration = Math.max(0,
                (baseGeneration + timeVariation + hourVariation) * timeOfDayFactor);
        double currentRevenue = baseRevenue + (timeVariation * 15000) + (hourVariation * 8000);

        SolarData data = new SolarData("Sunrise Solar Farm", currentGeneration, currentRevenue);

        logger.debug("Generated public solar data: {} MW, ${}",
                String.format("%.1f", currentGeneration),
                String.format("%.0f", currentRevenue));

        return data;
    }

    public Map<String, Object> getPublicStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Generate realistic statistics with some variation
        long currentHour = System.currentTimeMillis() / (1000 * 60 * 60);
        double variation = Math.sin(currentHour * 0.1) * 0.1;

        stats.put("totalPlants", 3);
        stats.put("totalGeneration", Math.round((24.7 + variation * 2) * 10.0) / 10.0);
        stats.put("totalCapacity", 30.0);
        stats.put("totalRevenue", Math.round((375000.0 + variation * 25000) * 100.0) / 100.0);
        stats.put("averageEfficiency", Math.round((82.3 + variation * 3) * 10.0) / 10.0);
        stats.put("onlinePlants", 3);
        stats.put("lastUpdated", java.time.Instant.now().toString());

        // Add some seasonal statistics
        java.time.Month currentMonth = java.time.LocalDate.now().getMonth();
        double seasonalFactor = getSeasonalFactor(currentMonth);
        stats.put("seasonalEfficiency", Math.round(seasonalFactor * 85.0 * 10.0) / 10.0);

        logger.debug("Generated public statistics: {} plants, {} MW total",
                stats.get("totalPlants"), stats.get("totalGeneration"));

        return stats;
    }

    /**
     * Get seasonal efficiency factor based on current month
     */
    private double getSeasonalFactor(java.time.Month month) {
        switch (month) {
            case DECEMBER:
            case JANUARY:
            case FEBRUARY:
                return 0.75; // Winter - lower efficiency
            case MARCH:
            case APRIL:
            case MAY:
                return 0.95; // Spring - good efficiency
            case JUNE:
            case JULY:
            case AUGUST:
                return 1.1; // Summer - peak efficiency
            case SEPTEMBER:
            case OCTOBER:
            case NOVEMBER:
                return 0.9; // Fall - good efficiency
            default:
                return 1.0;
        }
    }
}