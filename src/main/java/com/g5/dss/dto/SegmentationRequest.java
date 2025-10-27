package com.g5.dss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request for customer segmentation analysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentationRequest {
    
    private Integer numberOfSegments; // Default: 5
    private Double minSupport; // For association rules, default: 0.01
    private Double minConfidence; // For association rules, default: 0.3
    private LocalDate startDate;
    private LocalDate endDate;
    private String segmentationMethod; // RFM, KMEANS, etc.
    private Boolean includeRecommendations; // Include product recommendations
    private String callbackUrl; // Optional callback URL
}
