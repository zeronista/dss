package com.g5.dss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Generic request for prediction tasks
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequest {
    
    private String predictionType; // SALES_FORECAST, CHURN_PREDICTION, RISK_ASSESSMENT, etc.
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> customerIds;
    private List<String> productIds;
    private List<String> countries;
    private Integer forecastDays;
    private Boolean excludeCancellations;
    private String callbackUrl; // Optional callback URL for async notification
}
