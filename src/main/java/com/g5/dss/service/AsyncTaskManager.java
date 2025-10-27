package com.g5.dss.service;

import com.g5.dss.dto.AsyncTaskResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage async task tracking and results
 */
@Service
public class AsyncTaskManager {

    private final Map<String, AsyncTaskResponse<?>> taskStore = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<?>> futureStore = new ConcurrentHashMap<>();

    /**
     * Generate a unique task ID
     */
    public String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Store task information
     */
    public <T> void storeTask(String taskId, AsyncTaskResponse<T> response) {
        taskStore.put(taskId, response);
    }

    /**
     * Store CompletableFuture for a task
     */
    public <T> void storeFuture(String taskId, CompletableFuture<T> future) {
        futureStore.put(taskId, future);
    }

    /**
     * Get task status and result
     */
    @SuppressWarnings("unchecked")
    public <T> AsyncTaskResponse<T> getTask(String taskId) {
        return (AsyncTaskResponse<T>) taskStore.get(taskId);
    }

    /**
     * Update task status
     */
    public <T> void updateTask(String taskId, AsyncTaskResponse<T> response) {
        taskStore.put(taskId, response);
    }

    /**
     * Remove completed task
     */
    public void removeTask(String taskId) {
        taskStore.remove(taskId);
        futureStore.remove(taskId);
    }

    /**
     * Cancel a running task
     */
    public boolean cancelTask(String taskId) {
        CompletableFuture<?> future = futureStore.get(taskId);
        if (future != null && !future.isDone()) {
            boolean cancelled = future.cancel(true);
            if (cancelled) {
                AsyncTaskResponse<?> response = taskStore.get(taskId);
                if (response != null) {
                    response.setStatus("CANCELLED");
                    response.setMessage("Task was cancelled by user");
                }
            }
            return cancelled;
        }
        return false;
    }

    /**
     * Get all tasks
     */
    public Map<String, AsyncTaskResponse<?>> getAllTasks() {
        return new ConcurrentHashMap<>(taskStore);
    }

    /**
     * Clear all completed tasks
     */
    public void clearCompletedTasks() {
        taskStore.entrySet().removeIf(entry -> 
            "COMPLETED".equals(entry.getValue().getStatus()) || 
            "FAILED".equals(entry.getValue().getStatus()) ||
            "CANCELLED".equals(entry.getValue().getStatus())
        );
    }
}
