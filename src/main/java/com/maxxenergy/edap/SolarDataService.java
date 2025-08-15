package com.maxxenergy.edap;

import com.maxxenergy.edap.model.SolarData;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PreDestroy;

@Service
public class SolarDataService {

    private MongoClient mongoClient;

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:maxxenergy}")
    private String databaseName;

    private MongoClient getMongoClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(mongoUri);
        }
        return mongoClient;
    }

    @PreDestroy
    public void closeMongoClient() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public SolarData getPublicData() {
        try {
            MongoDatabase database = getMongoClient().getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("solar_plants");

            Document publicPlant = collection.find(new Document("isPublic", true)).first();

            if (publicPlant != null) {
                return new SolarData(
                        publicPlant.getString("plantName"),
                        publicPlant.getDouble("generation"),
                        publicPlant.getDouble("revenue")
                );
            }
        } catch (Exception e) {
            System.err.println("Error fetching data from MongoDB: " + e.getMessage());
            e.printStackTrace();
        }


        return new SolarData("Demo Solar Plant", 8.5, 125000.0);
    }
}