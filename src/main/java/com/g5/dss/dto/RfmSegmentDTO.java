package com.g5.dss.dto;

public class RfmSegmentDTO {
    
    private String customerId;
    private String customerName;
    private Integer recency;
    private Integer frequency;
    private Double monetary;
    private String segment;
    private String recommendation;

    // Constructors
    public RfmSegmentDTO() {}

    public RfmSegmentDTO(String customerId, String customerName, Integer recency, 
                         Integer frequency, Double monetary, String segment) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.recency = recency;
        this.frequency = frequency;
        this.monetary = monetary;
        this.segment = segment;
    }

    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getRecency() { return recency; }
    public void setRecency(Integer recency) { this.recency = recency; }

    public Integer getFrequency() { return frequency; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }

    public Double getMonetary() { return monetary; }
    public void setMonetary(Double monetary) { this.monetary = monetary; }

    public String getSegment() { return segment; }
    public void setSegment(String segment) { this.segment = segment; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}

