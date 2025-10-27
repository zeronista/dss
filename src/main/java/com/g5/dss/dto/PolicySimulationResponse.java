package com.g5.dss.dto;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for policy simulation results
 */
public class PolicySimulationResponse {
    
    private Double threshold;
    private Double totalExpectedProfit;
    private Integer totalOrders;
    private Integer ordersImpacted;
    private Double ordersImpactedPct;
    private Double revenueAtRisk;
    
    // Breakdown
    private Double totalRevenueIfApproved;
    private Double totalRevenueIfBlocked;
    private Double profitGainVsBaseline;
    
    // For chart data
    private List<ThresholdDataPoint> profitCurve;
    
    public PolicySimulationResponse() {}
    
    // Inner class for chart data points
    public static class ThresholdDataPoint {
        private Double threshold;
        private Double profit;
        private Integer ordersImpacted;
        private Double revenueImpacted;
        
        public ThresholdDataPoint() {}
        
        public ThresholdDataPoint(Double threshold, Double profit, Integer ordersImpacted, Double revenueImpacted) {
            this.threshold = threshold;
            this.profit = profit;
            this.ordersImpacted = ordersImpacted;
            this.revenueImpacted = revenueImpacted;
        }
        
        // Getters and Setters
        public Double getThreshold() {
            return threshold;
        }
        
        public void setThreshold(Double threshold) {
            this.threshold = threshold;
        }
        
        public Double getProfit() {
            return profit;
        }
        
        public void setProfit(Double profit) {
            this.profit = profit;
        }
        
        public Integer getOrdersImpacted() {
            return ordersImpacted;
        }
        
        public void setOrdersImpacted(Integer ordersImpacted) {
            this.ordersImpacted = ordersImpacted;
        }
        
        public Double getRevenueImpacted() {
            return revenueImpacted;
        }
        
        public void setRevenueImpacted(Double revenueImpacted) {
            this.revenueImpacted = revenueImpacted;
        }
    }
    
    // Getters and Setters
    
    public Double getThreshold() {
        return threshold;
    }
    
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
    
    public Double getTotalExpectedProfit() {
        return totalExpectedProfit;
    }
    
    public void setTotalExpectedProfit(Double totalExpectedProfit) {
        this.totalExpectedProfit = totalExpectedProfit;
    }
    
    public Integer getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public Integer getOrdersImpacted() {
        return ordersImpacted;
    }
    
    public void setOrdersImpacted(Integer ordersImpacted) {
        this.ordersImpacted = ordersImpacted;
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
    
    public Double getTotalRevenueIfApproved() {
        return totalRevenueIfApproved;
    }
    
    public void setTotalRevenueIfApproved(Double totalRevenueIfApproved) {
        this.totalRevenueIfApproved = totalRevenueIfApproved;
    }
    
    public Double getTotalRevenueIfBlocked() {
        return totalRevenueIfBlocked;
    }
    
    public void setTotalRevenueIfBlocked(Double totalRevenueIfBlocked) {
        this.totalRevenueIfBlocked = totalRevenueIfBlocked;
    }
    
    public Double getProfitGainVsBaseline() {
        return profitGainVsBaseline;
    }
    
    public void setProfitGainVsBaseline(Double profitGainVsBaseline) {
        this.profitGainVsBaseline = profitGainVsBaseline;
    }
    
    public List<ThresholdDataPoint> getProfitCurve() {
        return profitCurve;
    }
    
    public void setProfitCurve(List<ThresholdDataPoint> profitCurve) {
        this.profitCurve = profitCurve;
    }
}
