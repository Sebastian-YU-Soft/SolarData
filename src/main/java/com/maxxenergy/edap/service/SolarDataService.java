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