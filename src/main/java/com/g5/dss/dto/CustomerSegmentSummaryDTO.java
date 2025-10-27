package com.g5.dss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO tổng hợp thông tin phân khúc khách hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSegmentSummaryDTO {
    private String segmentName;
    private Integer customerCount;
    private Double totalValue;
    private Double avgRecency;
    private Double avgFrequency;
    private Double avgMonetary;
    
    // Characteristics
    private String description;
    private List<String> marketingActions;
    
    // Percentage
    private Double percentageOfTotal;
}
