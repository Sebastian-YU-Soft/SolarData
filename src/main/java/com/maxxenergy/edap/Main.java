package com.maxxenergy.edap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the MAXX Energy EDAP application.
 *
 * This Spring Boot application provides a comprehensive portal for solar energy data,
 * insights, and management tools with role-based access control using in-memory storage.
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting MAXX Energy EDAP (In-Memory Version)...");
        SpringApplication.run(Main.class, args);
        System.out.println("MAXX Energy EDAP started successfully!");
        System.out.println("Access the application at: http://localhost:8080");
    }
}