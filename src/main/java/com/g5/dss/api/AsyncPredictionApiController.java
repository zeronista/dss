package com.g5.dss.api;

import com.g5.dss.dto.*;
import com.g5.dss.service.AsyncTaskManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Async API Controller for Predictions and Forecasting
 * Handles: Sales Forecast, Customer Churn Prediction, Return Risk Assessment
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/async/predictions")
@RequiredArgsConstructor
public class AsyncPredictionApiController {

    private final AsyncTaskManager taskManager;
    private final RestTemplate restTemplate;

    @Qualifier("predictionExecutor")
    private final java.util.concurrent.Executor predictionExecutor;

    /**
     * Submit async sales forecast request
     * POST /api/v1/async/predictions/sales-forecast
     */
    @PostMapping("/sales-forecast")
    public ResponseEntity<AsyncTaskResponse<Void>> submitSalesForecast(
            @RequestBody PredictionRequest request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<Map<String, Object>> future = processSalesForecast(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Submit async churn prediction request
     * POST /api/v1/async/predictions/churn
     */
    @PostMapping("/churn")
    public ResponseEntity<AsyncTaskResponse<Void>> submitChurnPrediction(
            @RequestBody PredictionRequest request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<Map<String, Object>> future = processChurnPrediction(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Submit async return risk assessment
     * POST /api/v1/async/predictions/return-risk
     */
    @PostMapping("/return-risk")
    public ResponseEntity<AsyncTaskResponse<Void>> submitReturnRisk(
            @RequestBody OrderRiskRequest request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<RiskAssessmentResponse> future = processReturnRisk(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Get task status and result
     * GET /api/v1/async/predictions/{taskId}
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
     * DELETE /api/v1/async/predictions/{taskId}
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

    /**
     * Get all tasks
     * GET /api/v1/async/predictions
     */
    @GetMapping
    public ResponseEntity<Map<String, AsyncTaskResponse<?>>> getAllTasks() {
        return ResponseEntity.ok(taskManager.getAllTasks());
    }

    // ==================== ASYNC PROCESSING METHODS ====================

    @Async("predictionExecutor")
    public CompletableFuture<Map<String, Object>> processSalesForecast(
            String taskId, PredictionRequest request) {
        
        log.info("Starting sales forecast task: {}", taskId);
        
        try {
            // Update status to processing
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            // Call model service
            String modelServiceUrl = "http://localhost:8000/api/forecast/sales";
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            Map<String, Object> result = restTemplate.postForObject(
                modelServiceUrl, request, Map.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            // Store completed result
            AsyncTaskResponse<Map<String, Object>> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed sales forecast task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in sales forecast task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    @Async("predictionExecutor")
    public CompletableFuture<Map<String, Object>> processChurnPrediction(
            String taskId, PredictionRequest request) {
        
        log.info("Starting churn prediction task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/predict/churn";
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            Map<String, Object> result = restTemplate.postForObject(
                modelServiceUrl, request, Map.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<Map<String, Object>> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed churn prediction task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in churn prediction task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    @Async("predictionExecutor")
    public CompletableFuture<RiskAssessmentResponse> processReturnRisk(
            String taskId, OrderRiskRequest request) {
        
        log.info("Starting return risk assessment task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/risk/return";
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            RiskAssessmentResponse result = restTemplate.postForObject(
                modelServiceUrl, request, RiskAssessmentResponse.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<RiskAssessmentResponse> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed return risk assessment task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in return risk assessment task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }
}
