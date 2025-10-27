package com.g5.dss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Model Service API endpoints
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "model-service")
public class ModelServiceProperties {

    /**
     * Base URL for model service
     */
    private String baseUrl = "http://localhost:8000";

    /**
     * Timeout for API calls (milliseconds)
     */
    private Integer timeout = 30000;

    /**
     * Port for prediction services
     */
    private Integer predictionPort = 8000;

    /**
     * Port for analytics services
     */
    private Integer analyticsPort = 8001;

    /**
     * API endpoints configuration
     */
    private Endpoints endpoints = new Endpoints();

    @Data
    public static class Endpoints {
        // Prediction endpoints
        private String salesForecast = "/api/forecast/sales";
        private String churnPrediction = "/api/predict/churn";
        private String returnRisk = "/api/risk/return";

        // Segmentation endpoints
        private String segmentationAnalyze = "/api/segmentation/analyze";
        private String rfmAnalysis = "/api/rfm/analyze";
        private String recommendations = "/api/recommendations/generate";
        private String marketBasket = "/api/market-basket/analyze";

        // Anomaly detection endpoints
        private String anomalyDetect = "/api/anomaly/detect";
        private String invoiceAudit = "/api/audit/invoice";

        // Policy simulation endpoints
        private String policySimulate = "/api/policy/simulate";
        private String optimalThreshold = "/api/policy/optimal-threshold";
        private String batchEvaluate = "/api/policy/batch-evaluate";
    }

    /**
     * Get full URL for an endpoint
     */
    public String getFullUrl(String endpoint) {
        return baseUrl + endpoint;
    }

    /**
     * Get full URL with custom port
     */
    public String getFullUrl(Integer port, String endpoint) {
        String baseWithoutPort = baseUrl.replaceAll(":\\d+$", "");
        return baseWithoutPort + ":" + port + endpoint;
    }
}
