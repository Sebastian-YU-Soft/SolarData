package com.maxxenergy.edap.config;

import com.maxxenergy.edap.service.SolarDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;

/**
 * Component to initialize sample data on application startup.
 * Ensures the application has baseline data for demonstration purposes.
 */
@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private SolarDataService solarDataService;

    /**
     * Initialize sample data after application context is loaded
     */
    @PostConstruct
    public void initializeData() {
        logger.info("Starting data initialization...");

        try {
            // The SolarDataService already handles sample data creation in its @PostConstruct method
            // This component ensures the service is properly initialized

            // Test that we can retrieve data
            var testData = solarDataService.getPublicData();
            logger.info("Sample data initialized successfully. Test data: {} - {} MW",
                    testData.getPlantName(), testData.getGeneration());

        } catch (Exception e) {
            logger.warn("Could not initialize sample data: {}", e.getMessage());
            logger.info("Application will continue with default fallback data");
        }

        logger.info("Data initialization complete");
    }
}