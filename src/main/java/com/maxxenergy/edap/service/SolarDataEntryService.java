// Fixed SolarDataService.java - corrected package name
package com.maxxenergy.edap.service;

import com.maxxenergy.edap.model.SolarData;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.HashMap;

/**
 * Service for retrieving public solar data.
 * Handles MongoDB connection and data retrieval for public dashboard.
 */
@Service
public class SolarDataService {

    private static final Logger logger = LoggerFactory.getLogger(SolarDataService.class);

    private MongoClient mongoClient;

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:maxxenergy}")
    private String databaseName;

    @PostConstruct
    public void initialize() {
        logger.info("Initializing SolarDataService with database: {}", databaseName);
        try {
            ensureSampleData();
        } catch (Exception e) {
            logger.warn("Could not initialize sample data: {}", e.getMessage());
        }
    }

    private MongoClient getMongoClient() {
        if (mongoClient == null) {
            logger.info("Creating MongoDB client connection to: {}", mongoUri);
            try {
                mongoClient = MongoClients.create(mongoUri);
            } catch (Exception e) {
                logger.warn("Could not create MongoDB connection: {}", e.getMessage());
            }
        }
        return mongoClient;
    }

    @PreDestroy
    public void closeMongoClient() {
        if (mongoClient != null) {
            logger.info("Closing MongoDB client connection");
            mongoClient.close();
        }
    }

    public SolarData getPublicData() {
        try {
            MongoClient client = getMongoClient();
            if (client != null) {
                MongoDatabase database = client.getDatabase(databaseName);
                MongoCollection<Document> collection = database.getCollection("solar_plants");

                Document publicPlant = collection.find(new Document("isPublic", true)).first();
                if (publicPlant != null) {
                    logger.debug("Found public plant data: {}", publicPlant.getString("plantName"));
                    return new SolarData(
                            publicPlant.getString("plantName"),
                            publicPlant.getDouble("generation"),
                            publicPlant.getDouble("revenue")
                    );
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching data from MongoDB: {}", e.getMessage());
        }

        logger.info("Returning default demo data");
        return createDefaultDemoData();
    }

    private SolarData createDefaultDemoData() {
        long currentMinute = System.currentTimeMillis() / (1000 * 60);
        double variation = Math.sin(currentMinute * 0.1) * 0.5;
        double baseGeneration = 8.5;
        double baseRevenue = 125000.0;

        return new SolarData(
                "Demo Solar Plant",
                Math.max(0, baseGeneration + variation),
                baseRevenue + (variation * 10000)
        );
    }

    private void ensureSampleData() {
        try {
            MongoClient client = getMongoClient();
            if (client != null) {
                MongoDatabase database = client.getDatabase(databaseName);
                MongoCollection<Document> collection = database.getCollection("solar_plants");

                long count = collection.countDocuments();
                if (count == 0) {
                    logger.info("No solar plant data found, creating sample data");
                    createSampleData(collection);
                } else {
                    logger.info("Found {} existing solar plant records", count);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not ensure sample data exists: {}", e.getMessage());
        }
    }

    private void createSampleData(MongoCollection<Document> collection) {
        try {
            Document publicPlant = new Document()
                    .append("plantName", "Sunrise Solar Farm")
                    .append("generation", 12.3)
                    .append("capacity", 15.0)
                    .append("efficiency", 82.0)
                    .append("revenue", 156000.0)
                    .append("location", "Arizona, USA")
                    .append("isPublic", true)
                    .append("createdAt", new java.util.Date());

            collection.insertOne(publicPlant);
            logger.info("Created sample solar plant data");
        } catch (Exception e) {
            logger.warn("Could not create sample data: {}", e.getMessage());
        }
    }

    public Map<String, Object> getPublicStatistics() {
        Map<String, Object> stats = new HashMap<>();
        try {
            MongoClient client = getMongoClient();
            if (client != null) {
                // MongoDB logic here
                stats.put("totalPlants", 1);
                stats.put("totalGeneration", 8.5);
                stats.put("totalCapacity", 10.0);
                stats.put("totalRevenue", 125000.0);
                stats.put("averageEfficiency", 85.0);
            } else {
                // Default stats when MongoDB is not available
                stats.put("totalPlants", 1);
                stats.put("totalGeneration", 8.5);
                stats.put("totalCapacity", 10.0);
                stats.put("totalRevenue", 125000.0);
                stats.put("averageEfficiency", 85.0);
            }
        } catch (Exception e) {
            logger.error("Error fetching statistics: {}", e.getMessage());
            stats.put("error", "Could not fetch statistics");
        }
        return stats;
    }
}

// Fixed UserService.java - corrected package name
package com.maxxenergy.edap.service;

import com.maxxenergy.edap.model.User;
import com.maxxenergy.edap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service class for user management operations.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    public User registerUser(String name, String email, String password) throws Exception {
        validateUserInput(name, email, password);

        String normalizedEmail = email.toLowerCase().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new Exception("Email address is already registered");
        }

        String passwordHash = hashPassword(password);
        User user = new User(name.trim(), normalizedEmail, passwordHash);
        user.setRole("staff");

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: SHA-256 algorithm not available", e);
        }
    }

    private void validateUserInput(String name, String email, String password) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Full name is required");
        }
        if (name.trim().length() < 2) {
            throw new Exception("Name must be at least 2 characters long");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email address is required");
        }
        if (!isValidEmail(email.trim())) {
            throw new Exception("Please enter a valid email address");
        }

        validatePassword(password);
    }

    private void validatePassword(String password) throws Exception {
        if (password == null || password.isEmpty()) {
            throw new Exception("Password is required");
        }
        if (password.length() < 8) {
            throw new Exception("Password must be at least 8 characters long");
        }

        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        if (!hasLetter || !hasDigit) {
            throw new Exception("Password must contain at least one letter and one number");
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}