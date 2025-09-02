package com.maxxenergy.edap.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Model class for solar data entries submitted by users.
 * Contains comprehensive solar plant performance data.
 */
@Document(collection = "solar_data_entries")
public class SolarDataEntry {

    @Id
    private String id;

    @Indexed
    @NotBlank(message = "User ID is required")
    private String userId;

    @JsonProperty("plantName")
    @NotBlank(message = "Plant name is required")
    @Size(min = 2, max = 100, message = "Plant name must be between 2 and 100 characters")
    private String plantName;

    @JsonProperty("generation")
    @DecimalMin(value = "0.0", message = "Generation cannot be negative")
    @NotNull(message = "Generation is required")
    private Double generation; // Current power generation in MW

    @JsonProperty("capacity")
    @DecimalMin(value = "0.1", message = "Capacity must be positive")
    @NotNull(message = "Capacity is required")
    private Double capacity; // Plant capacity in MW

    @JsonProperty("efficiency")
    @DecimalMin(value = "0.0", message = "Efficiency cannot be negative")
    @DecimalMax(value = "100.0", message = "Efficiency cannot exceed 100%")
    @NotNull(message = "Efficiency is required")
    private Double efficiency; // Efficiency percentage

    @JsonProperty("temperature")
    @DecimalMin(value = "-50.0", message = "Temperature seems unrealistic")
    @DecimalMax(value = "70.0", message = "Temperature seems unrealistic")
    private Double temperature = 25.0; // Temperature in Celsius, default 25°C

    @JsonProperty("irradiance")
    @DecimalMin(value = "0.0", message = "Irradiance cannot be negative")
    @DecimalMax(value = "1500.0", message = "Irradiance seems unrealistic")
    private Double irradiance = 1000.0; // Solar irradiance in W/m², default 1000

    @JsonProperty("revenue")
    @DecimalMin(value = "0.0", message = "Revenue cannot be negative")
    private Double revenue = 0.0; // Revenue generated in dollars

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Indexed
    private boolean isPublic = false; // Whether this data is publicly viewable

    private String notes; // Optional notes about the data entry

    // Calculated fields
    private Double capacityUtilization; // Generation / Capacity * 100

    // Default constructor for MongoDB and JSON deserialization
    public SolarDataEntry() {
        this.timestamp = LocalDateTime.now();
        this.temperature = 25.0;
        this.irradiance = 1000.0;
        this.revenue = 0.0;
        this.isPublic = false;
    }

    /**
     * Constructor with required fields
     */
    public SolarDataEntry(String userId, String plantName, Double generation,
                          Double capacity, Double efficiency) {
        this();
        this.userId = userId;
        this.plantName = plantName;
        this.generation = generation;
        this.capacity = capacity;
        this.efficiency = efficiency;
        calculateDerivedFields();
    }

    /**
     * Calculate derived fields like capacity utilization
     */
    public void calculateDerivedFields() {
        if (capacity != null && capacity > 0 && generation != null) {
            this.capacityUtilization = (generation / capacity) * 100.0;
        }
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public Double getGeneration() {
        return generation;
    }

    public void setGeneration(Double generation) {
        this.generation = generation;
        calculateDerivedFields();
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
        calculateDerivedFields();
    }

    public Double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Double efficiency) {
        this.efficiency = efficiency;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature != null ? temperature : 25.0;
    }

    public Double getIrradiance() {
        return irradiance;
    }

    public void setIrradiance(Double irradiance) {
        this.irradiance = irradiance != null ? irradiance : 1000.0;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue != null ? revenue : 0.0;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getCapacityUtilization() {
        return capacityUtilization;
    }

    /**
     * Get a formatted summary of this data entry
     */
    public String getSummary() {
        return String.format("%s: %.1f MW (%.1f%% efficiency) at %s",
                plantName, generation, efficiency,
                timestamp.toString().substring(0, 16));
    }

    /**
     * Validate the data entry for business rules
     */
    public boolean isValid() {
        return userId != null && !userId.trim().isEmpty() &&
                plantName != null && !plantName.trim().isEmpty() &&
                generation != null && generation >= 0 &&
                capacity != null && capacity > 0 &&
                efficiency != null && efficiency >= 0 && efficiency <= 100 &&
                timestamp != null;
    }

    @Override
    public String toString() {
        return "SolarDataEntry{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", plantName='" + plantName + '\'' +
                ", generation=" + generation + " MW" +
                ", capacity=" + capacity + " MW" +
                ", efficiency=" + efficiency + "%" +
                ", temperature=" + temperature + "°C" +
                ", irradiance=" + irradiance + " W/m²" +
                ", revenue=$" + revenue +
                ", timestamp=" + timestamp +
                ", isPublic=" + isPublic +
                ", capacityUtilization=" + (capacityUtilization != null ?
                String.format("%.1f%%", capacityUtilization) : "N/A") +
                '}';
    }
}