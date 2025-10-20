package com.g5.dss.dto;

import java.util.List;

public class RecommendationDTO {
    
    private String customerId;
    private String productId;
    private String productName;
    private Double confidenceScore;
    private String reason;
    private List<String> relatedProducts;

    // Constructors
    public RecommendationDTO() {}

    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public List<String> getRelatedProducts() { return relatedProducts; }
    public void setRelatedProducts(List<String> relatedProducts) { this.relatedProducts = relatedProducts; }
}

