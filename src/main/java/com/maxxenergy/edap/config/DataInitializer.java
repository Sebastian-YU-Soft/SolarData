package com.maxxenergy.edap.config;

import com.maxxenergy.edap.service.SolarDataService;
import com.maxxenergy.edap.service.UserService;
import com.maxxenergy.edap.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;

/**
 * Component to initialize sample data on application startup.
 * Creates demo users and ensures the application has baseline data.
 */
@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private SolarDataService solarDataService;

    @Autowired
    private UserService userService;

    /**
     * Initialize sample data after application context is loaded
     */
    @PostConstruct
    public void initializeData() {
        logger.info("Starting in-memory data initialization...");

        try {
            createSampleUsers();

            // Test that we can retrieve solar data
            var testData = solarDataService.getPublicData();
            logger.info("Solar data service initialized successfully. Test data: {} - {} MW",
                    testData.getPlantName(), testData.getGeneration());

        } catch (Exception e) {
            logger.warn("Could not initialize sample data: {}", e.getMessage());
            logger.info("Application will continue with minimal data");
        }

        logger.info("Data initialization complete");
    }

    /**
     * Create sample users for demonstration
     */
    private void createSampleUsers() {
        try {
            // Create admin user if it doesn't exist
            if (!userService.findByEmail("admin@maxxenergy.com").isPresent()) {
                User admin = userService.registerUser("Admin User", "admin@maxxenergy.com", "Admin123!");
                admin.setRole("executive");
                admin.setDepartment("Administration");
                admin.setLocation("New York, NY");
                admin.setJobTitle("System Administrator");
                userService.updateUser(admin);
                logger.info("Created admin user: admin@maxxenergy.com (password: Admin123!)");
            }

            // Create demo user if it doesn't exist
            if (!userService.findByEmail("demo@maxxenergy.com").isPresent()) {
                User demo = userService.registerUser("Demo User", "demo@maxxenergy.com", "Demo123!");
                demo.setRole("staff");
                demo.setDepartment("Solar Operations");
                demo.setLocation("Phoenix, AZ");
                demo.setJobTitle("Solar Technician");
                userService.updateUser(demo);
                logger.info("Created demo user: demo@maxxenergy.com (password: Demo123!)");
            }

            // Create manager user if it doesn't exist
            if (!userService.findByEmail("manager@maxxenergy.com").isPresent()) {
                User manager = userService.registerUser("Manager User", "manager@maxxenergy.com", "Manager123!");
                manager.setRole("manager");
                manager.setDepartment("Operations");
                manager.setLocation("Austin, TX");
                manager.setJobTitle("Operations Manager");
                userService.updateUser(manager);
                logger.info("Created manager user: manager@maxxenergy.com (password: Manager123!)");
            }

            logger.info("Sample users created successfully");

        } catch (Exception e) {
            logger.warn("Could not create sample users: {}", e.getMessage());
        }
    }
}