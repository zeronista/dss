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
 * Async API Controller for Anomaly Detection and Inventory Audit
 * Handles: Transaction Anomalies, Invoice Quality Checks
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/async/anomaly")
@RequiredArgsConstructor
public class AsyncAnomalyApiController {

    private final AsyncTaskManager taskManager;
    private final RestTemplate restTemplate;

    @Qualifier("anomalyExecutor")
    private final java.util.concurrent.Executor anomalyExecutor;

    /**
     * Submit async anomaly detection request
     * POST /api/v1/async/anomaly/detect
     */
    @PostMapping("/detect")
    public ResponseEntity<AsyncTaskResponse<Void>> submitAnomalyDetection(
            @RequestBody Map<String, Object> request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<List<AnomalyDTO>> future = processAnomalyDetection(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Submit async invoice quality audit
     * POST /api/v1/async/anomaly/invoice-audit
     */
    @PostMapping("/invoice-audit")
    public ResponseEntity<AsyncTaskResponse<Void>> submitInvoiceAudit(
            @RequestBody Map<String, Object> request) {
        
        String taskId = taskManager.generateTaskId();
        AsyncTaskResponse<Void> response = AsyncTaskResponse.pending(taskId);
        taskManager.storeTask(taskId, response);

        CompletableFuture<Map<String, Object>> future = processInvoiceAudit(taskId, request);
        taskManager.storeFuture(taskId, future);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Get task status and result
     * GET /api/v1/async/anomaly/{taskId}
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
     * DELETE /api/v1/async/anomaly/{taskId}
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

    @Async("anomalyExecutor")
    public CompletableFuture<List<AnomalyDTO>> processAnomalyDetection(
            String taskId, Map<String, Object> request) {
        
        log.info("Starting anomaly detection task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/anomaly/detect";
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            @SuppressWarnings("unchecked")
            List<AnomalyDTO> result = restTemplate.postForObject(
                modelServiceUrl, request, List.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<List<AnomalyDTO>> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed anomaly detection task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in anomaly detection task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    @Async("anomalyExecutor")
    public CompletableFuture<Map<String, Object>> processInvoiceAudit(
            String taskId, Map<String, Object> request) {
        
        log.info("Starting invoice audit task: {}", taskId);
        
        try {
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 10));

            String modelServiceUrl = "http://localhost:8000/api/audit/invoice";
            
            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 50));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = restTemplate.postForObject(
                modelServiceUrl, request, Map.class);

            taskManager.updateTask(taskId, AsyncTaskResponse.processing(taskId, 90));
            
            AsyncTaskResponse<Map<String, Object>> completedResponse = 
                AsyncTaskResponse.completed(taskId, result);
            taskManager.updateTask(taskId, completedResponse);

            log.info("Completed invoice audit task: {}", taskId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("Error in invoice audit task: {}", taskId, e);
            taskManager.updateTask(taskId, AsyncTaskResponse.failed(taskId, e.getMessage()));
            throw new RuntimeException(e);
        }
    }
}
