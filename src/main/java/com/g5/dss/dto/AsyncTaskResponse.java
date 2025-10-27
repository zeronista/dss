package com.g5.dss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic response for async task submissions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskResponse<T> {
    
    private String taskId;
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private String message;
    private LocalDateTime submittedAt;
    private LocalDateTime completedAt;
    private T result;
    private String errorMessage;
    private Integer progress; // 0-100
    
    public static <T> AsyncTaskResponse<T> pending(String taskId) {
        return AsyncTaskResponse.<T>builder()
                .taskId(taskId)
                .status("PENDING")
                .message("Task submitted successfully")
                .submittedAt(LocalDateTime.now())
                .progress(0)
                .build();
    }
    
    public static <T> AsyncTaskResponse<T> processing(String taskId, int progress) {
        return AsyncTaskResponse.<T>builder()
                .taskId(taskId)
                .status("PROCESSING")
                .message("Task is being processed")
                .progress(progress)
                .build();
    }
    
    public static <T> AsyncTaskResponse<T> completed(String taskId, T result) {
        return AsyncTaskResponse.<T>builder()
                .taskId(taskId)
                .status("COMPLETED")
                .message("Task completed successfully")
                .completedAt(LocalDateTime.now())
                .result(result)
                .progress(100)
                .build();
    }
    
    public static <T> AsyncTaskResponse<T> failed(String taskId, String error) {
        return AsyncTaskResponse.<T>builder()
                .taskId(taskId)
                .status("FAILED")
                .message("Task failed")
                .errorMessage(error)
                .completedAt(LocalDateTime.now())
                .build();
    }
}
