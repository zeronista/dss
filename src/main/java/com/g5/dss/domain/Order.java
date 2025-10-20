package com.g5.dss.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
public class Order {
    
    @Id
    private String id;
    private String orderId;
    private String customerId;
    private LocalDateTime orderDate;
    private String status;
    private Double totalAmount;
    private String paymentMethod;
    private List<OrderItem> items;
    private Boolean isReturned;
    private Double returnRiskScore;

    // Constructors
    public Order() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public Boolean getIsReturned() { return isReturned; }
    public void setIsReturned(Boolean isReturned) { this.isReturned = isReturned; }

    public Double getReturnRiskScore() { return returnRiskScore; }
    public void setReturnRiskScore(Double returnRiskScore) { this.returnRiskScore = returnRiskScore; }
}

