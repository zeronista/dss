package com.g5.dss.api;

import com.g5.dss.dto.*;
import com.g5.dss.service.AsyncTaskManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Async API Controller for Customer Segmentation and Recommendations
 * Handles: RFM Segmentation, Product Recommendations, Marketing Actions
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/async/segmentation")
@RequiredArgsConstructor
public class AsyncSegmentationApiController {

    private final AsyncTaskManager taskManager;
    private final RestTemplate restTemplate;

    @Qualifier("segmentationExecutor")
    private final java.util.concurrent.Executor segmentationExecutor;

    /**
     * Submit async customer segmentation request
     * POST /api/v1/async/segmentation/analyze
     */
    @PostMapping("/analyze")
    public ResponseEntity<AsyncTaskResponse<Void>> submitSegmentation(
            @RequestBody SegmentationRequest request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<Map<String, Object>> future = processSegmentation(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Submit async RFM analysis request
     * POST /api/v1/async/segmentation/rfm
     */
    @PostMapping("/rfm")
    public ResponseEntity<AsyncTaskResponse<Void>> submitRFMAnalysis(
            @RequestBody SegmentationRequest request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<List<RfmSegmentDTO>> future = processRFMAnalysis(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Submit async product recommendation request
     * POST /api/v1/async/segmentation/recommendations
     */
    @PostMapping("/recommendations")
    public ResponseEntity<AsyncTaskResponse<Void>> submitRecommendations(
            @RequestBody SegmentationRequest request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<List<RecommendationDTO>> future = 
            processRecommendations(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Submit async market basket analysis
     * POST /api/v1/async/segmentation/market-basket
     */
    @PostMapping("/market-basket")
    public ResponseEntity<AsyncTaskResponse<Void>> submitMarketBasket(
            @RequestBody SegmentationRequest request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<List<MarketBasketRuleDTO>> future = 
            processMarketBasket(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Get task status and result
     * GET /api/v1/async/segmentation/{taskId}
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<AsyncTaskResponse<?>> getTaskStatus(@PathVariable String taskId) {
        AsyncTaskResponse<?> task = taskManager.getTask(taskId);
        
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(task);
    }

    /**
     * Cancel a running task
     * DELETE /api/v1/async/segmentation/{taskId}
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Map<String, String>> cancelTask(@PathVariable String taskId) {
        boolean cancelled = taskManager.cancelTask(taskId);
        
        Map<String, String> response = new HashMap<>();
        if (cancelled) {
            response.put("status", "cancelled");
            response.put("message", "Task cancelled successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "failed");
            response.put("message", "Task not found or already completed");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ASYNC PROCESSING METHODS ====================

    @Async("segmentationExecutor")
    public CompletableFuture<Map<String, Object>> processSegmentation(
            String taskId, SegmentationRequest request) {
        
        log.info("Starting customer segmentation task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/segmentation/analyze";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("numberOfSegments", request.getNumberOfSegments());
            requestBody.put("minSupport", request.getMinSupport());
            requestBody.put("minConfidence", request.getMinConfidence());
            requestBody.put("startDate", request.getStartDate());
            requestBody.put("endDate", request.getEndDate());
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = restTemplate.postForObject(
                modelServiceUrl, requestBody, Map.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<Map<String, Object>> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed customer segmentation task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in customer segmentation task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    @Async("segmentationExecutor")
    public CompletableFuture<List<RfmSegmentDTO>> processRFMAnalysis(
            String taskId, SegmentationRequest request) {
        
        log.info("Starting RFM analysis task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/rfm/analyze";
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            @SuppressWarnings("unchecked")
            List<RfmSegmentDTO> result = restTemplate.postForObject(
                modelServiceUrl, request, List.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<List<RfmSegmentDTO>> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed RFM analysis task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in RFM analysis task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    @Async("segmentationExecutor")
    public CompletableFuture<List<RecommendationDTO>> processRecommendations(
            String taskId, SegmentationRequest request) {
        
        log.info("Starting product recommendations task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/recommendations/generate";
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            @SuppressWarnings("unchecked")
            List<RecommendationDTO> result = restTemplate.postForObject(
                modelServiceUrl, request, List.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<List<RecommendationDTO>> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed product recommendations task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in product recommendations task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    @Async("segmentationExecutor")
    public CompletableFuture<List<MarketBasketRuleDTO>> processMarketBasket(
            String taskId, SegmentationRequest request) {
        
        log.info("Starting market basket analysis task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/market-basket/analyze";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("minSupport", request.getMinSupport());
            requestBody.put("minConfidence", request.getMinConfidence());
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            @SuppressWarnings("unchecked")
            List<MarketBasketRuleDTO> result = restTemplate.postForObject(
                modelServiceUrl, requestBody, List.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<List<MarketBasketRuleDTO>> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed market basket analysis task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in market basket analysis task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }
}
