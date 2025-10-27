package com.g5.dss.domain.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity cho MySQL table online_retail
 */
@Entity
@Table(name = "online_retail", indexes = {
    @Index(name = "idx_invoice_no", columnList = "invoice_no"),
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_invoice_date", columnList = "invoice_date"),
    @Index(name = "idx_country", columnList = "country")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineRetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "invoice_no", length = 20, nullable = false)
    private String invoiceNo;
    
    @Column(name = "stock_code", length = 50, nullable = false)
    private String stockCode;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;
    
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;
    
    @Column(name = "customer_id")
    private Integer customerId;
    
    @Column(name = "country", length = 100, nullable = false)
    private String country;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Tính tổng tiền
     */
    public BigDecimal getTotalAmount() {
        if (quantity != null && unitPrice != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}
