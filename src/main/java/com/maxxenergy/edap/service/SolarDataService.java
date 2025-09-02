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

    /**
     * Initialize MongoDB connection and sample data
     */
    @PostConstruct
    public void initialize() {
        logger.info("Initializing SolarDataService with database: {}", databaseName);
        try {
            ensureSampleData();
        } catch (Exception e) {
            logger.warn("Could not initialize sample data: {}", e.getMessage());
        }
    }

    /**
     * Get MongoDB client with lazy initialization
     */
    private MongoClient getMongoClient() {
        if (mongoClient == null) {
            logger.info("Creating MongoDB client connection to: {}", mongoUri);
            mongoClient = MongoClients.create(mongoUri);
        }
        return mongoClient;
    }

    /**
     * Close MongoDB client on shutdown
     */
    @PreDestroy
    public void closeMongoClient() {
        if (mongoClient != null) {
            logger.info("Closing MongoDB client connection");
            mongoClient.close();
        }
    }

    /**
     * Get public solar data for dashboard display
     * @return SolarData object with current public plant information
     */
    public SolarData getPublicData() {
        try {
            MongoDatabase database = getMongoClient().getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("solar_plants");

            // Try to find a public plant
            Document publicPlant = collection.find(new Document("isPublic", true)).first();

            if (publicPlant != null) {
                logger.debug("Found public plant data: {}", publicPlant.getString("plantName"));
                return new SolarData(
                        publicPlant.getString("plantName"),
                        publicPlant.getDouble("generation"),
                        publicPlant.getDouble("revenue")
                );
            } else {
                logger.debug("No public plant found, checking for any plant data");
                // Fallback to any plant data
                Document anyPlant = collection.find().first();
                if (anyPlant != null) {
                    return new SolarData(
                            anyPlant.getString("plantName"),
                            anyPlant.getDouble("generation"),
                            anyPlant.getDouble("revenue")
                    );
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching data from MongoDB: {}", e.getMessage(), e);
        }

        // Return realistic default data if database is unavailable
        logger.info("Returning default demo data");
        return createDefaultDemoData();
    }

    /**
     * Create default demo data for when database is unavailable
     */
    private SolarData createDefaultDemoData() {
        // Generate semi-realistic values that change slightly over time
        long currentMinute = System.currentTimeMillis() / (1000 * 60);
        double variation = Math.sin(currentMinute * 0.1) * 0.5; // Small variation

        double baseGeneration = 8.5;
        double baseRevenue = 125000.0;

        return new SolarData(
                "Demo Solar Plant",
                Math.max(0, baseGeneration + variation),
                baseRevenue + (variation * 10000)
        );
    }

    /**
     * Ensure sample data exists in the database
     */
    private void ensureSampleData() {
        try {
            MongoDatabase database = getMongoClient().getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("solar_plants");

            // Check if any data exists
            long count = collection.countDocuments();
            if (count == 0) {
                logger.info("No solar plant data found, creating sample data");
                createSampleData(collection);
            } else {
                logger.info("Found {} existing solar plant records", count);
            }
        } catch (Exception e) {
            logger.warn("Could not ensure sample data exists: {}", e.getMessage());
        }
    }

    /**
     * Create sample solar plant data
     */
    private void createSampleData(MongoCollection<Document> collection) {
        Document publicPlant = new Document()
                .append("plantName", "Sunrise Solar Farm")
                .append("generation", 12.3)
                .append("capacity", 15.0)
                .append("efficiency", 82.0)
                .append("revenue", 156000.0)
                .append("location", "Arizona, USA")
                .append("isPublic", true)
                .append("createdAt", new java.util.Date());

        Document privatePlant = new Document()
                .append("plantName", "Corporate Solar Installation")
                .append("generation", 8.7)
                .append("capacity", 10.0)
                .append("efficiency", 87.0)
                .append("revenue", 112000.0)
                .append("location", "California, USA")
                .append("isPublic", false)
                .append("createdAt", new java.util.Date());

        try {
            collection.insertMany(java.util.Arrays.asList(publicPlant, privatePlant));
            logger.info("Created sample solar plant data");
        } catch (Exception e) {
            logger.warn("Could not create sample data: {}", e.getMessage());
        }
    }

    /**
     * Update public plant data (for administrative use)
     * @param plantName Name of the plant
     * @param generation Current generation in MW
     * @param revenue Current revenue
     * @return Updated SolarData
     */
    public SolarData updatePublicData(String plantName, double generation, double revenue) {
        try {
            MongoDatabase database = getMongoClient().getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("solar_plants");

            Document filter = new Document("isPublic", true);
            Document update = new Document("$set", new Document()
                    .append("plantName", plantName)
                    .append("generation", generation)
                    .append("revenue", revenue)
                    .append("lastUpdated", new java.util.Date()));

            collection.updateOne(filter, update);
            logger.info("Updated public plant data: {} - {} MW - ${}", plantName, generation, revenue);

            return new SolarData(plantName, generation, revenue);
        } catch (Exception e) {
            logger.error("Error updating public data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update public data", e);
        }
    }

    /**
     * Get aggregated statistics from all public plants
     * @return Map containing statistics
     */
    public java.util.Map<String, Object> getPublicStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        try {
            MongoDatabase database = getMongoClient().getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("solar_plants");

            // Get all public plants
            java.util.List<Document> publicPlants = collection
                    .find(new Document("isPublic", true))
                    .into(new java.util.ArrayList<>());

            if (!publicPlants.isEmpty()) {
                double totalGeneration = publicPlants.stream()
                        .mapToDouble(doc -> doc.getDouble("generation"))
                        .sum();

                double totalCapacity = publicPlants.stream()
                        .mapToDouble(doc -> doc.getDouble("capacity"))
                        .sum();

                double totalRevenue = publicPlants.stream()
                        .mapToDouble(doc -> doc.getDouble("revenue"))
                        .sum();

                double avgEfficiency = publicPlants.stream()
                        .mapToDouble(doc -> doc.getDouble("efficiency"))
                        .average()
                        .orElse(0.0);

                stats.put("totalPlants", publicPlants.size());
                stats.put("totalGeneration", Math.round(totalGeneration * 10.0) / 10.0);
                stats.put("totalCapacity", Math.round(totalCapacity * 10.0) / 10.0);
                stats.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);
                stats.put("averageEfficiency", Math.round(avgEfficiency * 10.0) / 10.0);
                stats.put("overallUtilization", totalCapacity > 0 ?
                        Math.round((totalGeneration / totalCapacity * 100.0) * 10.0) / 10.0 : 0.0);
            } else {
                // Default stats
                stats.put("totalPlants", 1);
                stats.put("totalGeneration", 8.5);
                stats.put("totalCapacity", 10.0);
                stats.put("totalRevenue", 125000.0);
                stats.put("averageEfficiency", 85.0);
                stats.put("overallUtilization", 85.0);
            }
        } catch (Exception e) {
            logger.error("Error fetching public statistics: {}", e.getMessage(), e);
            // Return default stats on error
            stats.put("error", "Could not fetch statistics");
            stats.put("totalPlants", 0);
            stats.put("totalGeneration", 0.0);
            stats.put("totalCapacity", 0.0);
            stats.put("totalRevenue", 0.0);
            stats.put("averageEfficiency", 0.0);
            stats.put("overallUtilization", 0.0);
        }

        return stats;
    }
}