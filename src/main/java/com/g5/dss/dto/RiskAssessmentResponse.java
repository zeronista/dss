package com.g5.dss.dto;

import java.util.Map;

/**
 * Response DTO for risk assessment
 */
public class RiskAssessmentResponse {
    
    private String orderId;
    private Double riskScore;
    private String riskLevel;
    private String recommendedAction;
    private String actionReason;
    
    // Profit analysis
    private Double expectedProfitIfApproved;
    private Double expectedProfitIfBlocked;
    private Double profitDifference;
    
    // Features breakdown
    private Map<String, Object> features;
    
    // Policy info
    private String policyId;
    private Double thresholdUsed;
    
    public RiskAssessmentResponse() {}
    
    // Getters and Setters
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public Double getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public String getRecommendedAction() {
        return recommendedAction;
    }
    
    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }
    
    public String getActionReason() {
        return actionReason;
    }
    
    public void setActionReason(String actionReason) {
        this.actionReason = actionReason;
    }
    
    public Double getExpectedProfitIfApproved() {
        return expectedProfitIfApproved;
    }
    
    public void setExpectedProfitIfApproved(Double expectedProfitIfApproved) {
        this.expectedProfitIfApproved = expectedProfitIfApproved;
    }
    
    public Double getExpectedProfitIfBlocked() {
        return expectedProfitIfBlocked;
    }
    
    public void setExpectedProfitIfBlocked(Double expectedProfitIfBlocked) {
        this.expectedProfitIfBlocked = expectedProfitIfBlocked;
    }
    
    public Double getProfitDifference() {
        return profitDifference;
    }
    
    public void setProfitDifference(Double profitDifference) {
        this.profitDifference = profitDifference;
    }
    
    public Map<String, Object> getFeatures() {
        return features;
    }
    
    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }
    
    public String getPolicyId() {
        return policyId;
    }
    
    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }
    
    public Double getThresholdUsed() {
        return thresholdUsed;
    }
    
    public void setThresholdUsed(Double thresholdUsed) {
        this.thresholdUsed = thresholdUsed;
    }
}
