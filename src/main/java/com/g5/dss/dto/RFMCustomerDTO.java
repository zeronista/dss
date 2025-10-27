package com.g5.dss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho phân tích RFM (Recency, Frequency, Monetary)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RFMCustomerDTO {
    private Integer customerId;
    private String country;
    
    // RFM metrics
    private Integer recency;        // Số ngày kể từ lần mua gần nhất
    private Integer frequency;      // Số lượng đơn hàng
    private Double monetary;        // Tổng chi tiêu
    
    // Segment info
    private String segment;         // Champions, Loyal, At-Risk, Hibernating, Regulars
    private Integer segmentId;      // 0-4 cho clustering
    
    // Additional metrics
    private String lastPurchaseDate;
    private Double avgOrderValue;
    private Integer totalQuantity;
}
