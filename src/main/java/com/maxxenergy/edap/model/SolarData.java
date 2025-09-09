package com.maxxenergy.edap.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Model class representing public solar data that can be displayed without authentication.
 * Used for the public dashboard and API endpoints.
 */
public class SolarData {

    @JsonProperty("plantName")
    @NotNull(message = "Plant name cannot be null")
    private String plantName;

    @JsonProperty("generation")
    @DecimalMin(value = "0.0", message = "Generation cannot be negative")
    private double generation;

    @JsonProperty("revenue")
    @DecimalMin(value = "0.0", message = "Revenue cannot be negative")
    private double revenue;

    // Default constructor for JSON deserialization
    public SolarData() {
        this.plantName = "Unknown Plant";
        this.generation = 0.0;
        this.revenue = 0.0;
    }

    /**
     * Constructor with validation and default handling
     * @param plantName Name of the solar plant
     * @param generation Current generation in MW
     * @param revenue Revenue generated in dollars
     */
    public SolarData(String plantName, double generation, double revenue) {
        this.plantName = plantName != null && !plantName.trim().isEmpty() ?
                plantName.trim() : "Unknown Plant";
        this.generation = Math.max(0, generation); // Ensure non-negative
        this.revenue = Math.max(0, revenue); // Ensure non-negative
    }

    // Getters and setters
    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName != null ? plantName.trim() : "Unknown Plant";
    }

    public double getGeneration() {
        return generation;
    }

    public void setGeneration(double generation) {
        this.generation = Math.max(0, generation);
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = Math.max(0, revenue);
    }

    /**
     * Calculate efficiency based on theoretical maximum
     * @param maxCapacity Maximum plant capacity in MW
     * @return Efficiency percentage
     */
    public double calculateEfficiency(double maxCapacity) {
        if (maxCapacity <= 0) return 0.0;
        return Math.min(100.0, (generation / maxCapacity) * 100.0);
    }

    @Override
    public String toString() {
        return "SolarData{" +
                "plantName='" + plantName + '\'' +
                ", generation=" + generation + " MW" +
                ", revenue=$" + String.format("%.2f", revenue) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SolarData that = (SolarData) obj;
        return Double.compare(that.generation, generation) == 0 &&
                Double.compare(that.revenue, revenue) == 0 &&
                Objects.equals(plantName, that.plantName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plantName, generation, revenue);
    }
}