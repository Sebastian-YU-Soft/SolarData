package com.maxxenergy.edap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Main entry point for the MAXX Energy EDAP application.
 *
 * This Spring Boot application provides a comprehensive portal for solar energy data,
 * insights, and management tools with role-based access control.
 */
@SpringBootApplication
@EnableMongoAuditing
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting MAXX Energy EDAP...");
        SpringApplication.run(Main.class, args);
        System.out.println("MAXX Energy EDAP started successfully!");
    }
}