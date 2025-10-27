package com.g5.dss.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Return Risk Score for orders
 * Stores risk assessment results for gatekeeping decisions
 */
@Document(collection = "return_risk_scores")
public class ReturnRiskScore {
    
    @Id
    private String id;
    
    private String orderId;
    private String customerId;
    private String stockCode;
    
    // Risk scoring
    private Double riskScore;           // 0-100 scale
    private String riskLevel;           // LOW, MEDIUM, HIGH
    
    // Features used for scoring
    private Double customerReturnRate;
    private Double skuReturnRate;
    private Double orderValue;
    private Boolean isFirstTimeCustomer;
    
    // Decision recommendation
    private String recommendedAction;    // APPROVE, REQUIRE_PREPAY, BLOCK_COD, CANCEL
    private String actionReason;
    
    // Expected profit analysis
    private Double expectedProfitIfApproved;
    private Double expectedProfitIfBlocked;
    
    // Order details
    private Double revenue;
    private Double cogs;
    private Double shippingCost;
    private Integer quantity;
    private String country;
    
    // Metadata
    private LocalDateTime scoredAt;
    private String scoredBy;
    private String modelVersion;
    private Map<String, Object> additionalFeatures;
    
    // Policy applied
    private String policyId;
    private Double thresholdUsed;
    
    // Actual outcome (for model refinement)
    private Boolean actualReturn;
    private LocalDateTime returnDate;
    
    // Constructors
    public ReturnRiskScore() {
        this.scoredAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getStockCode() {
        return stockCode;
    }
    
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
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
    
    public Double getCustomerReturnRate() {
        return customerReturnRate;
    }
    
    public void setCustomerReturnRate(Double customerReturnRate) {
        this.customerReturnRate = customerReturnRate;
    }
    
    public Double getSkuReturnRate() {
        return skuReturnRate;
    }
    
    public void setSkuReturnRate(Double skuReturnRate) {
        this.skuReturnRate = skuReturnRate;
    }
    
    public Double getOrderValue() {
        return orderValue;
    }
    
    public void setOrderValue(Double orderValue) {
        this.orderValue = orderValue;
    }
    
    public Boolean getIsFirstTimeCustomer() {
        return isFirstTimeCustomer;
    }
    
    public void setIsFirstTimeCustomer(Boolean isFirstTimeCustomer) {
        this.isFirstTimeCustomer = isFirstTimeCustomer;
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
    
    public Double getRevenue() {
        return revenue;
    }
    
    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
    
    public Double getCogs() {
        return cogs;
    }
    
    public void setCogs(Double cogs) {
        this.cogs = cogs;
    }
    
    public Double getShippingCost() {
        return shippingCost;
    }
    
    public void setShippingCost(Double shippingCost) {
        this.shippingCost = shippingCost;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public LocalDateTime getScoredAt() {
        return scoredAt;
    }
    
    public void setScoredAt(LocalDateTime scoredAt) {
        this.scoredAt = scoredAt;
    }
    
    public String getScoredBy() {
        return scoredBy;
    }
    
    public void setScoredBy(String scoredBy) {
        this.scoredBy = scoredBy;
    }
    
    public String getModelVersion() {
        return modelVersion;
    }
    
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
    
    public Map<String, Object> getAdditionalFeatures() {
        return additionalFeatures;
    }
    
    public void setAdditionalFeatures(Map<String, Object> additionalFeatures) {
        this.additionalFeatures = additionalFeatures;
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
    
    public Boolean getActualReturn() {
        return actualReturn;
    }
    
    public void setActualReturn(Boolean actualReturn) {
        this.actualReturn = actualReturn;
    }
    
    public LocalDateTime getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }
}
