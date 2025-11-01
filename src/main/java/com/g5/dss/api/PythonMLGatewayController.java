package com.g5.dss.api;

import com.g5.dss.service.PythonMLService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST API Gateway for Python ML Service Integration
 * 
 * This controller acts as a proxy/gateway between Java Spring Boot and Python FastAPI
 * for advanced analytics features (forecasting, segmentation, market basket, etc.)
 * 
 * Architecture:
 * - Java handles: Web UI, Authentication, CRUD, Business Logic
 * - Python handles: ML/AI, Forecasting, Advanced Analytics
 * - This controller: Communication bridge between both
 * 
 * Python API should run on: http://localhost:8000
 * Java API runs on: http://localhost:8080
 * 
 * @author G5 Team
 */
@RestController
@RequestMapping("/api/ml")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PythonMLGatewayController {
    
    private final PythonMLService pythonMLService;
    
    // ============================================
    // Health & Status
    // ============================================
    
    /**
     * Check Python ML service health
     * GET /api/ml/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkPythonHealth() {
        log.info("Checking Python ML service health");
        Map<String, Object> health = pythonMLService.checkHealth();
        return ResponseEntity.ok(health);
    }
    
    // ============================================
    // Data Upload & Session Management
    // ============================================
    
    /**
     * Upload CSV data to Python for analysis
     * POST /api/ml/upload
     * 
     * @param file CSV file to upload
     * @return Session ID and basic stats
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadData(@RequestParam("file") MultipartFile file) {
        log.info("Uploading file to Python ML service: {}", file.getOriginalFilename());
        
        try {
            Map<String, Object> result = pythonMLService.uploadData(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error uploading file to Python", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete a session from Python service
     * DELETE /api/ml/session/{sessionId}
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, Object>> deleteSession(@PathVariable String sessionId) {
        log.info("Deleting Python ML session: {}", sessionId);
        Map<String, Object> result = pythonMLService.deleteSession(sessionId);
        return ResponseEntity.ok(result);
    }
    
    // ============================================
    // Overview & KPIs
    // ============================================
    
    /**
     * Get overview metrics and insights
     * POST /api/ml/overview
     * 
     * @param request Contains sessionId, optional start/end dates
     */
    @PostMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview(@RequestBody Map<String, Object> request) {
        log.info("Getting overview from Python ML service");
        Map<String, Object> result = pythonMLService.getOverview(request);
        return ResponseEntity.ok(result);
    }
    
    // ============================================
    // Revenue Forecasting (SARIMAX)
    // ============================================
    
    /**
     * Forecast future revenue using SARIMAX model
     * POST /api/ml/forecast
     * 
     * @param request {
     *   sessionId: string,
     *   periods: int (default: 3),
     *   confidence_level: float (default: 0.90)
     * }
     * @return Forecast data with confidence intervals
     */
    @PostMapping("/forecast")
    public ResponseEntity<Map<String, Object>> forecastRevenue(@RequestBody Map<String, Object> request) {
        log.info("Requesting revenue forecast from Python ML service");
        Map<String, Object> result = pythonMLService.forecastRevenue(request);
        return ResponseEntity.ok(result);
    }
    
    // ============================================
    // RFM Analysis
    // ============================================
    
    /**
     * Calculate RFM (Recency, Frequency, Monetary) metrics
     * POST /api/ml/rfm
     * 
     * @param request {
     *   sessionId: string,
     *   reference_date: date (optional)
     * }
     * @return RFM metrics for all customers
     */
    @PostMapping("/rfm")
    public ResponseEntity<Map<String, Object>> calculateRFM(@RequestBody Map<String, Object> request) {
        log.info("Calculating RFM from Python ML service");
        Map<String, Object> result = pythonMLService.calculateRFM(request);
        return ResponseEntity.ok(result);
    }
    
    // ============================================
    // Customer Segmentation (K-Means)
    // ============================================
    
    /**
     * Perform customer segmentation using K-Means clustering
     * POST /api/ml/segmentation
     * 
     * @param request {
     *   sessionId: string,
     *   n_clusters: int (default: 4, range: 2-10),
     *   random_state: int (default: 42)
     * }
     * @return Customer segments with characteristics and marketing actions
     */
    @PostMapping("/segmentation")
    public ResponseEntity<Map<String, Object>> segmentCustomers(@RequestBody Map<String, Object> request) {
        log.info("Performing customer segmentation from Python ML service");
        Map<String, Object> result = pythonMLService.segmentCustomers(request);
        return ResponseEntity.ok(result);
    }
    
    // ============================================
    // Market Basket Analysis (Apriori)
    // ============================================
    
    /**
     * Analyze product associations using Apriori algorithm
     * POST /api/ml/market-basket
     * 
     * @param request {
     *   sessionId: string,
     *   min_support: float (default: 0.01),
     *   min_confidence: float (default: 0.2),
     *   max_rules: int (default: 10),
     *   customer_ids: array (optional - filter by customer segment)
     * }
     * @return Association rules for product bundling
     */
    @PostMapping("/market-basket")
    public ResponseEntity<Map<String, Object>> marketBasketAnalysis(@RequestBody Map<String, Object> request) {
        log.info("Performing market basket analysis from Python ML service");
        Map<String, Object> result = pythonMLService.marketBasketAnalysis(request);
        return ResponseEntity.ok(result);
    }
    
    // ============================================
    // Churn Prediction
    // ============================================
    
    /**
     * Identify customers at risk of churning
     * POST /api/ml/churn
     * 
     * @param request {
     *   sessionId: string,
     *   recency_threshold_pct: float (default: 75),
     *   frequency_threshold_pct: float (default: 25)
     * }
     * @return List of at-risk customers with recommended retention actions
     */
    @PostMapping("/churn")
    public ResponseEntity<Map<String, Object>> predictChurn(@RequestBody Map<String, Object> request) {
        log.info("Predicting customer churn from Python ML service");
        Map<String, Object> result = pythonMLService.predictChurn(request);
        return ResponseEntity.ok(result);
    }
    
    // ============================================
    // Download/Export
    // ============================================
    
    /**
     * Download RFM analysis results as CSV
     * GET /api/ml/download/rfm/{sessionId}
     */
    @GetMapping("/download/rfm/{sessionId}")
    public ResponseEntity<byte[]> downloadRFM(@PathVariable String sessionId) {
        log.info("Downloading RFM CSV from Python ML service");
        return pythonMLService.downloadRFMCsv(sessionId);
    }
    
    /**
     * Download customer segments as CSV
     * GET /api/ml/download/segments/{sessionId}
     * 
     * @param nClusters Number of clusters (optional, default: 4)
     */
    @GetMapping("/download/segments/{sessionId}")
    public ResponseEntity<byte[]> downloadSegments(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "4") int nClusters) {
        log.info("Downloading segments CSV from Python ML service");
        return pythonMLService.downloadSegmentsCsv(sessionId, nClusters);
    }
}
