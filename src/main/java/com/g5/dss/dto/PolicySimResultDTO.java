package com.g5.dss.dto;

import java.util.Map;

public class PolicySimResultDTO {
    
    private String orderId;
    private Double returnRiskScore;
    private String riskLevel; // LOW, MEDIUM, HIGH
    private Boolean shouldApprove;
    private String recommendation;
    private Map<String, Object> factors;

    // Constructors
    public PolicySimResultDTO() {}

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Double getReturnRiskScore() { return returnRiskScore; }
    public void setReturnRiskScore(Double returnRiskScore) { this.returnRiskScore = returnRiskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Boolean getShouldApprove() { return shouldApprove; }
    public void setShouldApprove(Boolean shouldApprove) { this.shouldApprove = shouldApprove; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public Map<String, Object> getFactors() { return factors; }
    public void setFactors(Map<String, Object> factors) { this.factors = factors; }
}

