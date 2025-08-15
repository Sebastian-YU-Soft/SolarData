package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SolarData {

    @JsonProperty("plantName")
    private String plantName;

    @JsonProperty("generation")
    private double generation;

    @JsonProperty("revenue")
    private double revenue;

    // Default constructor for JSON deserialization
    public SolarData() {}

    public SolarData(String plantName, double generation, double revenue) {
        this.plantName = plantName != null ? plantName : "Unknown Plant";
        this.generation = Math.max(0, generation); // Ensure non-negative
        this.revenue = Math.max(0, revenue); // Ensure non-negative
    }

    // Getters and setters
    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public double getGeneration() {
        return generation;
    }

    public void setGeneration(double generation) {
        this.generation = generation;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return "SolarData{" +
                "plantName='" + plantName + '\'' +
                ", generation=" + generation +
                ", revenue=" + revenue +
                '}';
    }
}