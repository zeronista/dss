package com.g5.dss.dto;

import java.time.LocalDateTime;

public class AnomalyDTO {
    
    private String productId;
    private String productName;
    private Double anomalyScore;
    private String anomalyType; // OVERSTOCKED, UNDERSTOCKED, PRICE_ANOMALY
    private String severity; // LOW, MEDIUM, HIGH
    private LocalDateTime detectedAt;
    private String description;

    // Constructors
    public AnomalyDTO() {}

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Double getAnomalyScore() { return anomalyScore; }
    public void setAnomalyScore(Double anomalyScore) { this.anomalyScore = anomalyScore; }

    public String getAnomalyType() { return anomalyType; }
    public void setAnomalyType(String anomalyType) { this.anomalyType = anomalyType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

