package com.maxxenergy.edap.controller;

import com.maxxenergy.edap.service.SolarDataService;
import com.maxxenergy.edap.model.SolarData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * REST controller for public solar data endpoints.
 * Provides access to aggregated solar plant data without authentication.
 */
@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:5500"})
public class SolarDataController {

    private static final Logger logger = LoggerFactory.getLogger(SolarDataController.class);

    @Autowired
    private SolarDataService solarDataService;

    /**
     * Get public solar data for dashboard display
     * @return Current solar generation and revenue data
     */
    @GetMapping("/data")
    public ResponseEntity<SolarData> getPublicSolarData() {
        try {
            logger.debug("Fetching public solar data");
            SolarData data = solarDataService.getPublicData();

            if (data == null) {
                logger.warn("No solar data available");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            logger.debug("Returning solar data: {}", data.getPlantName());
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            logger.error("Error fetching public solar data: {}", e.getMessage(), e);

            // Return default data on error to keep the frontend working
            SolarData fallbackData = new SolarData("Demo Solar Plant", 8.5, 125000.0);
            return ResponseEntity.ok(fallbackData);
        }
    }

    /**
     * Get aggregated public statistics
     * @return Statistics about all public solar plants
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getPublicStatistics() {
        try {
            logger.debug("Fetching public statistics");
            Map<String, Object> stats = solarDataService.getPublicStatistics();
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            logger.error("Error fetching public statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to fetch statistics"));
        }
    }

    /**
     * Health check endpoint for monitoring
     * @return Simple OK status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.debug("Health check requested");

        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "SolarDataController",
                "timestamp", System.currentTimeMillis()
        );

        return ResponseEntity.ok(health);
    }

    /**
     * Get current system time (useful for frontend synchronization)
     * @return Current server timestamp
     */
    @GetMapping("/time")
    public ResponseEntity<Map<String, Object>> getCurrentTime() {
        Map<String, Object> timeInfo = Map.of(
                "timestamp", System.currentTimeMillis(),
                "iso", java.time.Instant.now().toString(),
                "timezone", java.time.ZoneId.systemDefault().toString()
        );

        return ResponseEntity.ok(timeInfo);
    }

    /**
     * Get API information and available endpoints
     * @return API metadata
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = Map.of(
                "name", "MAXX Energy EDAP Public API",
                "version", "1.0.0",
                "description", "Public endpoints for solar energy data access",
                "endpoints", Map.of(
                        "/api/public/data", "Get current public solar data",
                        "/api/public/statistics", "Get aggregated public statistics",
                        "/api/public/health", "Service health check",
                        "/api/public/time", "Get current server time",
                        "/api/public/info", "Get API information"
                )
        );

        return ResponseEntity.ok(info);
    }
}