package com.g5.dss.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Return Policy Configuration
 * Stores gatekeeping policy rules and thresholds
 */
@Document(collection = "policy_configurations")
public class PolicyConfiguration {
    
    @Id
    private String id;
    
    private String policyName;
    private String description;
    
    // Optimal threshold
    private Double optimalThreshold;        // τ*
    
    // Decision thresholds
    private List<PolicyThreshold> thresholds;
    
    // Cost parameters
    private Double returnProcessingCost;
    private Double shippingCostDefault;
    private Double cogsRatio;               // % of revenue
    
    // Business impact
    private Double conversionRateImpact;    // % loss when blocking COD
    
    // Policy metadata
    private String country;                 // null = global
    private String channel;                 // null = all channels
    private Boolean isActive;
    private Boolean isDefault;
    
    // Simulation results
    private Double expectedProfitGain;      // monthly
    private Double ordersImpactedPct;
    private Double revenueAtRisk;
    
    // Audit trail
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime activatedAt;
    private String activatedBy;
    private LocalDateTime lastModified;
    
    public PolicyConfiguration() {
        this.createdAt = LocalDateTime.now();
        this.isActive = false;
    }
    
    // Inner class for threshold rules
    public static class PolicyThreshold {
        private Double minScore;
        private Double maxScore;
        private String action;      // APPROVE, REQUIRE_PREPAY, BLOCK_COD, CANCEL
        private String description;
        
        public PolicyThreshold() {}
        
        public PolicyThreshold(Double minScore, Double maxScore, String action, String description) {
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.action = action;
            this.description = description;
        }
        
        // Getters and Setters
        public Double getMinScore() {
            return minScore;
        }
        
        public void setMinScore(Double minScore) {
            this.minScore = minScore;
        }
        
        public Double getMaxScore() {
            return maxScore;
        }
        
        public void setMaxScore(Double maxScore) {
            this.maxScore = maxScore;
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
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPolicyName() {
        return policyName;
    }
    
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getOptimalThreshold() {
        return optimalThreshold;
    }
    
    public void setOptimalThreshold(Double optimalThreshold) {
        this.optimalThreshold = optimalThreshold;
    }
    
    public List<PolicyThreshold> getThresholds() {
        return thresholds;
    }
    
    public void setThresholds(List<PolicyThreshold> thresholds) {
        this.thresholds = thresholds;
    }
    
    public Double getReturnProcessingCost() {
        return returnProcessingCost;
    }
    
    public void setReturnProcessingCost(Double returnProcessingCost) {
        this.returnProcessingCost = returnProcessingCost;
    }
    
    public Double getShippingCostDefault() {
        return shippingCostDefault;
    }
    
    public void setShippingCostDefault(Double shippingCostDefault) {
        this.shippingCostDefault = shippingCostDefault;
    }
    
    public Double getCogsRatio() {
        return cogsRatio;
    }
    
    public void setCogsRatio(Double cogsRatio) {
        this.cogsRatio = cogsRatio;
    }
    
    public Double getConversionRateImpact() {
        return conversionRateImpact;
    }
    
    public void setConversionRateImpact(Double conversionRateImpact) {
        this.conversionRateImpact = conversionRateImpact;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public Double getExpectedProfitGain() {
        return expectedProfitGain;
    }
    
    public void setExpectedProfitGain(Double expectedProfitGain) {
        this.expectedProfitGain = expectedProfitGain;
    }
    
    public Double getOrdersImpactedPct() {
        return ordersImpactedPct;
    }
    
    public void setOrdersImpactedPct(Double ordersImpactedPct) {
        this.ordersImpactedPct = ordersImpactedPct;
    }
    
    public Double getRevenueAtRisk() {
        return revenueAtRisk;
    }
    
    public void setRevenueAtRisk(Double revenueAtRisk) {
        this.revenueAtRisk = revenueAtRisk;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }
    
    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }
    
    public String getActivatedBy() {
        return activatedBy;
    }
    
    public void setActivatedBy(String activatedBy) {
        this.activatedBy = activatedBy;
    }
    
    public LocalDateTime getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
}
