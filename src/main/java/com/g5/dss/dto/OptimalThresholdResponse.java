package com.g5.dss.dto;

import java.util.List;

/**
 * Response DTO for optimal threshold recommendation
 */
public class OptimalThresholdResponse {
    
    private Double optimalThreshold;            // τ*
    private Double maxExpectedProfit;
    private String recommendation;
    private Double profitProtectedPerMonth;
    
    // Policy table
    private List<PolicyRule> policyRules;
    
    // Confidence/sensitivity
    private Double confidenceScore;
    private String sensitivityNote;
    
    // Full profit curve for visualization
    private List<PolicySimulationResponse.ThresholdDataPoint> profitCurve;
    
    public OptimalThresholdResponse() {}
    
    // Inner class for policy rules
    public static class PolicyRule {
        private String scoreRange;
        private String action;
        private String description;
        
        public PolicyRule() {}
        
        public PolicyRule(String scoreRange, String action, String description) {
            this.scoreRange = scoreRange;
            this.action = action;
            this.description = description;
        }
        
        // Getters and Setters
        public String getScoreRange() {
            return scoreRange;
        }
        
        public void setScoreRange(String scoreRange) {
            this.scoreRange = scoreRange;
        }
        
        public String getAction() {
            return action;
        }
        
        public void setAction(String action) {
            this.action = action;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
    
    // Getters and Setters
    
    public Double getOptimalThreshold() {
        return optimalThreshold;
    }
    
    public void setOptimalThreshold(Double optimalThreshold) {
        this.optimalThreshold = optimalThreshold;
    }
    
    public Double getMaxExpectedProfit() {
        return maxExpectedProfit;
    }
    
    public void setMaxExpectedProfit(Double maxExpectedProfit) {
        this.maxExpectedProfit = maxExpectedProfit;
    }
    
    public String getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
    
    public Double getProfitProtectedPerMonth() {
        return profitProtectedPerMonth;
    }
    
    public void setProfitProtectedPerMonth(Double profitProtectedPerMonth) {
        this.profitProtectedPerMonth = profitProtectedPerMonth;
    }
    
    public List<PolicyRule> getPolicyRules() {
        return policyRules;
    }
    
    public void setPolicyRules(List<PolicyRule> policyRules) {
        this.policyRules = policyRules;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public String getSensitivityNote() {
        return sensitivityNote;
    }
    
    public void setSensitivityNote(String sensitivityNote) {
        this.sensitivityNote = sensitivityNote;
    }
    
    public List<PolicySimulationResponse.ThresholdDataPoint> getProfitCurve() {
        return profitCurve;
    }
    
    public void setProfitCurve(List<PolicySimulationResponse.ThresholdDataPoint> profitCurve) {
        this.profitCurve = profitCurve;
    }
}
