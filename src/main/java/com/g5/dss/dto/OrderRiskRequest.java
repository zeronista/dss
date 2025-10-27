package com.g5.dss.dto;

import java.util.Map;

/**
 * Request DTO for order risk assessment
 */
public class OrderRiskRequest {
    
    private String orderId;
    private String customerId;
    private String stockCode;
    private Integer quantity;
    private Double unitPrice;
    private String country;
    
    // Optional: override defaults
    private Double cogs;
    private Double shippingCost;
    
    public OrderRiskRequest() {}
    
    // Getters and Setters
    
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
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
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
}
