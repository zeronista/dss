package com.g5.dss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho Market Basket Analysis (Association Rules)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketBasketRuleDTO {
    // Product A (antecedent)
    private String productACode;
    private String productAName;
    
    // Product B (consequent)
    private String productBCode;
    private String productBName;
    
    // Rule metrics
    private Double support;         // Tỷ lệ giao dịch có cả A và B
    private Double confidence;      // Xác suất mua B khi đã mua A (%)
    private Double lift;            // Mức độ liên kết (>1 là tốt)
    
    // Additional info
    private Integer transactionCount;  // Số giao dịch có cả A và B
    private String recommendation;     // Gợi ý marketing
}
