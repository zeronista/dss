package com.g5.dss.api;

import com.g5.dss.dto.AsyncTaskResponse;
import com.g5.dss.service.AsyncTaskManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Gateway API Controller for managing all async tasks
 * Provides centralized task management and monitoring
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/async")
@RequiredArgsConstructor
public class AsyncTaskGatewayController {

    private final AsyncTaskManager taskManager;

    /**
     * Get status of any task by ID
     * GET /api/v1/async/tasks/{taskId}
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<AsyncTaskResponse<?>> getTaskStatus(@PathVariable String taskId) {
        AsyncTaskResponse<?> task = taskManager.getTask(taskId);
        
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(task);
    }

    /**
     * Get all tasks with optional status filter
     * GET /api/v1/async/tasks?status=PENDING,PROCESSING
     */
    @GetMapping("/tasks")
    public ResponseEntity<Map<String, AsyncTaskResponse<?>>> getAllTasks(
            @RequestParam(required = false) String status) {
        
        Map<String, AsyncTaskResponse<?>> allTasks = taskManager.getAllTasks();
        
        if (status != null && !status.isEmpty()) {
            String[] statuses = status.split(",");
            Map<String, AsyncTaskResponse<?>> filteredTasks = new HashMap<>();
            
            for (Map.Entry<String, AsyncTaskResponse<?>> entry : allTasks.entrySet()) {
                for (String s : statuses) {
                    if (s.trim().equalsIgnoreCase(entry.getValue().getStatus())) {
                        filteredTasks.put(entry.getKey(), entry.getValue());
                        break;
                    }
                }
            }
            
            return ResponseEntity.ok(filteredTasks);
        }
        
        return ResponseEntity.ok(allTasks);
    }

    /**
     * Cancel any task by ID
     * DELETE /api/v1/async/tasks/{taskId}
     */
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Map<String, String>> cancelTask(@PathVariable String taskId) {
        boolean cancelled = taskManager.cancelTask(taskId);
        
        Map<String, String> response = new HashMap<>();
        if (cancelled) {
            response.put("status", "cancelled");
            response.put("message", "Task cancelled successfully");
            response.put("taskId", taskId);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "failed");
            response.put("message", "Task not found or already completed");
            response.put("taskId", taskId);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Remove a completed/failed task from tracking
     * DELETE /api/v1/async/tasks/{taskId}/remove
     */
    @DeleteMapping("/tasks/{taskId}/remove")
    public ResponseEntity<Map<String, String>> removeTask(@PathVariable String taskId) {
        AsyncTaskResponse<?> task = taskManager.getTask(taskId);
        
        Map<String, String> response = new HashMap<>();
        if (task == null) {
            response.put("status", "failed");
            response.put("message", "Task not found");
            return ResponseEntity.notFound().build();
        }
        
        String status = task.getStatus();
        if ("PENDING".equals(status) || "PROCESSING".equals(status)) {
            response.put("status", "failed");
            response.put("message", "Cannot remove active task. Cancel it first.");
            return ResponseEntity.badRequest().body(response);
        }
        
        taskManager.removeTask(taskId);
        response.put("status", "success");
        response.put("message", "Task removed successfully");
        response.put("taskId", taskId);
        return ResponseEntity.ok(response);
    }

    /**
     * Clear all completed/failed tasks
     * POST /api/v1/async/tasks/clear-completed
     */
    @PostMapping("/tasks/clear-completed")
    public ResponseEntity<Map<String, Object>> clearCompletedTasks() {
        int beforeCount = taskManager.getAllTasks().size();
        taskManager.clearCompletedTasks();
        int afterCount = taskManager.getAllTasks().size();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Completed tasks cleared");
        response.put("tasksRemoved", beforeCount - afterCount);
        response.put("tasksRemaining", afterCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get task statistics
     * GET /api/v1/async/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTaskStatistics() {
        Map<String, AsyncTaskResponse<?>> allTasks = taskManager.getAllTasks();
        
        long pending = allTasks.values().stream()
            .filter(t -> "PENDING".equals(t.getStatus()))
            .count();
        
        long processing = allTasks.values().stream()
            .filter(t -> "PROCESSING".equals(t.getStatus()))
            .count();
        
        long completed = allTasks.values().stream()
            .filter(t -> "COMPLETED".equals(t.getStatus()))
            .count();
        
        long failed = allTasks.values().stream()
            .filter(t -> "FAILED".equals(t.getStatus()))
            .count();
        
        long cancelled = allTasks.values().stream()
            .filter(t -> "CANCELLED".equals(t.getStatus()))
            .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", allTasks.size());
        stats.put("pending", pending);
        stats.put("processing", processing);
        stats.put("completed", completed);
        stats.put("failed", failed);
        stats.put("cancelled", cancelled);
        stats.put("active", pending + processing);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Health check for async service
     * GET /api/v1/async/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Async Task Manager");
        health.put("timestamp", java.time.LocalDateTime.now());
        
        Map<String, AsyncTaskResponse<?>> allTasks = taskManager.getAllTasks();
        long activeTasks = allTasks.values().stream()
            .filter(t -> "PENDING".equals(t.getStatus()) || "PROCESSING".equals(t.getStatus()))
            .count();
        
        health.put("activeTasks", activeTasks);
        health.put("totalTasks", allTasks.size());
        
        return ResponseEntity.ok(health);
    }
}
