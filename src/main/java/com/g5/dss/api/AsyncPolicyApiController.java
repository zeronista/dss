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
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Async API Controller for Policy Simulation (Prescriptive DSS)
 * Handles: Return Risk Policy Simulation, Optimal Threshold Calculation
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/async/policy")
@RequiredArgsConstructor
public class AsyncPolicyApiController {

    private final AsyncTaskManager taskManager;
    private final RestTemplate restTemplate;

    @Qualifier("predictionExecutor")
    private final java.util.concurrent.Executor predictionExecutor;

    /**
     * Submit async policy simulation request
     * POST /api/v1/async/policy/simulate
     */
    @PostMapping("/simulate")
    public ResponseEntity<AsyncTaskResponse<Void>> submitPolicySimulation(
            @RequestBody PolicySimulationRequest request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<PolicySimulationResponse> future = 
            processPolicySimulation(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Submit async optimal threshold calculation
     * POST /api/v1/async/policy/optimal-threshold
     */
    @PostMapping("/optimal-threshold")
    public ResponseEntity<AsyncTaskResponse<Void>> submitOptimalThreshold(
            @RequestBody Map<String, Object> request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<OptimalThresholdResponse> future = 
            processOptimalThreshold(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Submit batch policy evaluation
     * POST /api/v1/async/policy/batch-evaluate
     */
    @PostMapping("/batch-evaluate")
    public ResponseEntity<AsyncTaskResponse<Void>> submitBatchEvaluation(
            @RequestBody Map<String, Object> request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<Map<String, Object>> future = 
            processBatchEvaluation(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Get task status and result
     * GET /api/v1/async/policy/{taskId}
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
     * DELETE /api/v1/async/policy/{taskId}
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

    @Async("predictionExecutor")
    public CompletableFuture<PolicySimulationResponse> processPolicySimulation(
            String taskId, PolicySimulationRequest request) {
        
        log.info("Starting policy simulation task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/policy/simulate";
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            PolicySimulationResponse result = restTemplate.postForObject(
                modelServiceUrl, request, PolicySimulationResponse.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<PolicySimulationResponse> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed policy simulation task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in policy simulation task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    @Async("predictionExecutor")
    public CompletableFuture<OptimalThresholdResponse> processOptimalThreshold(
            String taskId, Map<String, Object> request) {
        
        log.info("Starting optimal threshold calculation task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/policy/optimal-threshold";
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            OptimalThresholdResponse result = restTemplate.postForObject(
                modelServiceUrl, request, OptimalThresholdResponse.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<OptimalThresholdResponse> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed optimal threshold calculation task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in optimal threshold calculation task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    @Async("predictionExecutor")
    public CompletableFuture<Map<String, Object>> processBatchEvaluation(
            String taskId, Map<String, Object> request) {
        
        log.info("Starting batch policy evaluation task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/policy/batch-evaluate";
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = restTemplate.postForObject(
                modelServiceUrl, request, Map.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<Map<String, Object>> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed batch policy evaluation task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in batch policy evaluation task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }
}
