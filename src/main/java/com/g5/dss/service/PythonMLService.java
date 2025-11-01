package com.g5.dss.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service to communicate with Python FastAPI ML Service
 * 
 * This service handles all HTTP communication with the Python backend
 * which provides advanced analytics capabilities (ML/AI features).
 * 
 * Python Service URL: http://localhost:8000 (configurable)
 * 
 * Features handled by Python:
 * - Revenue Forecasting (SARIMAX)
 * - Customer Segmentation (K-Means)
 * - RFM Analysis
 * - Market Basket Analysis (Apriori)
 * - Churn Prediction
 * 
 * @author G5 Team
 */
@Service
@Slf4j
public class PythonMLService {
    
    @Value("${python.ml.service.url:http://localhost:8000}")
    private String pythonServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public PythonMLService() {
        this.restTemplate = new RestTemplate();
    }
    
    // ============================================
    // Health Check
    // ============================================
    
    /**
     * Check if Python ML service is healthy and running
     * 
     * @return Health status map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> checkHealth() {
        try {
            String url = pythonServiceUrl + "/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> result = new HashMap<>((Map<String, Object>) response.getBody());
                result.put("java_status", "healthy");
                result.put("python_url", pythonServiceUrl);
                return result;
            } else {
                return Map.of(
                    "java_status", "healthy",
                    "python_status", "unhealthy",
                    "error", "Python service returned status: " + response.getStatusCode()
                );
            }
        } catch (Exception e) {
            log.error("Python ML service health check failed", e);
            return Map.of(
                "java_status", "healthy",
                "python_status", "unreachable",
                "error", e.getMessage(),
                "python_url", pythonServiceUrl
            );
        }
    }
    
    // ============================================
    // Data Upload
    // ============================================
    
    /**
     * Upload CSV file to Python service for analysis
     * 
     * @param file MultipartFile containing CSV data
     * @return Upload result with session ID
     * @throws IOException if file reading fails
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadData(MultipartFile file) throws IOException {
        String url = pythonServiceUrl + "/api/upload";
        
        // Prepare multipart request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("file", resource);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            Map<String, Object> result = (Map<String, Object>) response.getBody();
            log.info("Data uploaded successfully. Session ID: {}", result.get("session_id"));
            return result;
        } catch (Exception e) {
            log.error("Error uploading data to Python service", e);
            throw new RuntimeException("Failed to upload data to Python ML service: " + e.getMessage());
        }
    }
    
    // ============================================
    // Session Management
    // ============================================
    
    /**
     * Delete a session from Python service
     * 
     * @param sessionId Session ID to delete
     * @return Deletion result
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> deleteSession(String sessionId) {
        String url = pythonServiceUrl + "/api/session/" + sessionId;
        
        try {
            restTemplate.delete(url);
            return Map.of("message", "Session deleted successfully", "session_id", sessionId);
        } catch (Exception e) {
            log.error("Error deleting session from Python service", e);
            return Map.of("error", e.getMessage(), "session_id", sessionId);
        }
    }
    
    // ============================================
    // Analytics Endpoints
    // ============================================
    
    /**
     * Get overview metrics from Python service
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getOverview(Map<String, Object> request) {
        return postRequest("/api/overview", request);
    }
    
    /**
     * Forecast revenue using SARIMAX model
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> forecastRevenue(Map<String, Object> request) {
        return postRequest("/api/forecast", request);
    }
    
    /**
     * Calculate RFM metrics
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> calculateRFM(Map<String, Object> request) {
        return postRequest("/api/rfm", request);
    }
    
    /**
     * Perform customer segmentation
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> segmentCustomers(Map<String, Object> request) {
        return postRequest("/api/segmentation", request);
    }
    
    /**
     * Analyze market basket (product associations)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> marketBasketAnalysis(Map<String, Object> request) {
        return postRequest("/api/market-basket", request);
    }
    
    /**
     * Predict customer churn
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> predictChurn(Map<String, Object> request) {
        return postRequest("/api/churn", request);
    }
    
    // ============================================
    // Download/Export
    // ============================================
    
    /**
     * Download RFM analysis as CSV
     */
    public ResponseEntity<byte[]> downloadRFMCsv(String sessionId) {
        String url = pythonServiceUrl + "/api/download/rfm/" + sessionId;
        return downloadCsv(url, "rfm_analysis_" + sessionId + ".csv");
    }
    
    /**
     * Download customer segments as CSV
     */
    public ResponseEntity<byte[]> downloadSegmentsCsv(String sessionId, int nClusters) {
        String url = pythonServiceUrl + "/api/download/segments/" + sessionId + "?n_clusters=" + nClusters;
        return downloadCsv(url, "segments_" + sessionId + ".csv");
    }
    
    // ============================================
    // Helper Methods
    // ============================================
    
    /**
     * Generic POST request to Python service
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> postRequest(String endpoint, Map<String, Object> request) {
        String url = pythonServiceUrl + endpoint;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            log.error("Error calling Python ML service endpoint: {}", endpoint, e);
            return Map.of(
                "error", "Failed to call Python ML service",
                "message", e.getMessage(),
                "endpoint", endpoint
            );
        }
    }
    
    /**
     * Download CSV file from Python service
     */
    private ResponseEntity<byte[]> downloadCsv(String url, String filename) {
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(filename)
                    .build());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response.getBody());
                    
        } catch (Exception e) {
            log.error("Error downloading CSV from Python service", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }
}
