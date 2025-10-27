package com.g5.dss.dto;

/**
 * Request DTO for policy simulation (What-if analysis)
 */
public class PolicySimulationRequest {
    
    private Double threshold;                   // τ to simulate
    private Double returnProcessingCost;
    private Double conversionRateImpact;        // % (0-100)
    private Double cogsRatio;                   // % of revenue
    private Double shippingCostDefault;
    
    // Filters
    private String country;
    private String dateFrom;                    // YYYY-MM-DD
    private String dateTo;
    private Integer sampleSize;                 // null = all
    
    public PolicySimulationRequest() {
        // Defaults
        this.returnProcessingCost = 15.0;
        this.conversionRateImpact = 20.0;
        this.cogsRatio = 60.0;
        this.shippingCostDefault = 5.0;
    }
    
    // Getters and Setters
    
    public Double getThreshold() {
        return threshold;
    }
    
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
    
    public Double getReturnProcessingCost() {
        return returnProcessingCost;
    }
    
    public void setReturnProcessingCost(Double returnProcessingCost) {
        this.returnProcessingCost = returnProcessingCost;
    }
    
    public Double getConversionRateImpact() {
        return conversionRateImpact;
    }
    
    public void setConversionRateImpact(Double conversionRateImpact) {
        this.conversionRateImpact = conversionRateImpact;
    }
    
    public Double getCogsRatio() {
        return cogsRatio;
    }
    
    public void setCogsRatio(Double cogsRatio) {
        this.cogsRatio = cogsRatio;
    }
    
    public Double getShippingCostDefault() {
        return shippingCostDefault;
    }
    
    public void setShippingCostDefault(Double shippingCostDefault) {
        this.shippingCostDefault = shippingCostDefault;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getDateFrom() {
        return dateFrom;
    }
    
    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }
    
    public String getDateTo() {
        return dateTo;
    }
    
    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }
    
    public Integer getSampleSize() {
        return sampleSize;
    }
    
    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }
}
