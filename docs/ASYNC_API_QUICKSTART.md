# Quick Start - Async API Usage

## 1. Cấu Hình Ban Đầu

### Bước 1: Thêm dependency vào pom.xml
```xml
<!-- Spring WebFlux for Async Processing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### Bước 2: Cấu hình application.yml
```yaml
async:
  core-pool-size: 5
  max-pool-size: 20
  queue-capacity: 100

model-service:
  base-url: http://localhost:8000
  timeout: 30000
  prediction-port: 8000
  analytics-port: 8001
```

## 2. Các API Endpoint Chính

### Predictions (Dự đoán)
- `/api/v1/async/predictions/sales-forecast` - Dự báo doanh thu
- `/api/v1/async/predictions/churn` - Dự đoán khách hàng rời bỏ
- `/api/v1/async/predictions/return-risk` - Đánh giá rủi ro trả hàng

### Segmentation (Phân khúc)
- `/api/v1/async/segmentation/analyze` - Phân khúc khách hàng
- `/api/v1/async/segmentation/rfm` - Phân tích RFM
- `/api/v1/async/segmentation/recommendations` - Gợi ý sản phẩm
- `/api/v1/async/segmentation/market-basket` - Phân tích giỏ hàng

### Anomaly Detection (Phát hiện bất thường)
- `/api/v1/async/anomaly/detect` - Phát hiện giao dịch bất thường
- `/api/v1/async/anomaly/invoice-audit` - Kiểm toán hóa đơn

### Policy Simulation (Mô phỏng chính sách)
- `/api/v1/async/policy/simulate` - Mô phỏng chính sách
- `/api/v1/async/policy/optimal-threshold` - Tính ngưỡng tối ưu
- `/api/v1/async/policy/batch-evaluate` - Đánh giá hàng loạt

### Task Management (Quản lý task)
- `GET /api/v1/async/tasks/{taskId}` - Xem trạng thái task
- `GET /api/v1/async/tasks` - Xem tất cả tasks
- `DELETE /api/v1/async/tasks/{taskId}` - Hủy task
- `GET /api/v1/async/stats` - Thống kê tasks
- `GET /api/v1/async/health` - Health check

## 3. Ví Dụ Sử Dụng

### Ví dụ 1: Dự báo Doanh Thu

**Bước 1: Submit task**
```bash
curl -X POST http://localhost:8080/api/v1/async/predictions/sales-forecast \
  -H "Content-Type: application/json" \
  -d '{
    "predictionType": "SALES_FORECAST",
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "forecastDays": 30,
    "excludeCancellations": true
  }'
```

**Response:**
```json
{
  "taskId": "abc-123-def-456",
  "status": "PENDING",
  "message": "Task submitted successfully",
  "submittedAt": "2024-10-27T10:30:00",
  "progress": 0
}
```

**Bước 2: Kiểm tra trạng thái**
```bash
curl http://localhost:8080/api/v1/async/tasks/abc-123-def-456
```

**Response (Processing):**
```json
{
  "taskId": "abc-123-def-456",
  "status": "PROCESSING",
  "progress": 50
}
```

**Bước 3: Lấy kết quả (khi hoàn thành)**
```bash
curl http://localhost:8080/api/v1/async/tasks/abc-123-def-456
```

**Response (Completed):**
```json
{
  "taskId": "abc-123-def-456",
  "status": "COMPLETED",
  "message": "Task completed successfully",
  "completedAt": "2024-10-27T10:35:00",
  "progress": 100,
  "result": {
    "forecast": [...],
    "metrics": {...}
  }
}
```

### Ví dụ 2: Phân khúc Khách hàng

```bash
curl -X POST http://localhost:8080/api/v1/async/segmentation/rfm \
  -H "Content-Type: application/json" \
  -d '{
    "numberOfSegments": 5,
    "startDate": "2024-01-01",
    "endDate": "2024-12-31"
  }'
```

### Ví dụ 3: Mô phỏng Chính sách

```bash
curl -X POST http://localhost:8080/api/v1/async/policy/simulate \
  -H "Content-Type: application/json" \
  -d '{
    "riskThreshold": 75,
    "returnProcessingCost": 50.0,
    "conversionRateImpact": 0.15
  }'
```

### Ví dụ 4: Kiểm tra Health & Stats

```bash
# Health check
curl http://localhost:8080/api/v1/async/health

# Statistics
curl http://localhost:8080/api/v1/async/stats
```

## 4. Workflow trong Frontend

### JavaScript/TypeScript Example

```javascript
// 1. Submit task
async function submitForecast(requestData) {
  const response = await fetch('/api/v1/async/predictions/sales-forecast', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(requestData)
  });
  
  const { taskId } = await response.json();
  return taskId;
}

// 2. Poll for status
async function pollTaskStatus(taskId) {
  const pollInterval = 2000; // 2 seconds
  const maxAttempts = 150; // 5 minutes total
  
  for (let i = 0; i < maxAttempts; i++) {
    const response = await fetch(`/api/v1/async/tasks/${taskId}`);
    const task = await response.json();
    
    if (task.status === 'COMPLETED') {
      return task.result;
    } else if (task.status === 'FAILED') {
      throw new Error(task.errorMessage);
    }
    
    // Update progress bar
    updateProgressBar(task.progress);
    
    // Wait before next poll
    await new Promise(resolve => setTimeout(resolve, pollInterval));
  }
  
  throw new Error('Task timeout');
}

// 3. Complete workflow
async function runForecastAnalysis() {
  try {
    showLoader('Submitting task...');
    
    const taskId = await submitForecast({
      predictionType: 'SALES_FORECAST',
      startDate: '2024-01-01',
      endDate: '2024-12-31',
      forecastDays: 30
    });
    
    showLoader('Processing... 0%');
    
    const result = await pollTaskStatus(taskId);
    
    hideLoader();
    displayResults(result);
    
  } catch (error) {
    hideLoader();
    showError(error.message);
  }
}
```

## 5. Testing với Postman

### Collection Structure
```
DSS Async APIs
├── Predictions
│   ├── Submit Sales Forecast
│   ├── Submit Churn Prediction
│   └── Submit Return Risk
├── Segmentation
│   ├── Submit RFM Analysis
│   └── Submit Recommendations
├── Policy
│   └── Submit Policy Simulation
└── Task Management
    ├── Get Task Status
    ├── Get All Tasks
    ├── Cancel Task
    └── Get Stats
```

### Environment Variables
```
baseUrl: http://localhost:8080
apiVersion: v1
taskId: {{taskId}}  # Auto-captured from submit response
```

## 6. Monitoring & Troubleshooting

### Kiểm tra số lượng task active
```bash
curl http://localhost:8080/api/v1/async/stats
```

### Xóa các task đã hoàn thành
```bash
curl -X POST http://localhost:8080/api/v1/async/tasks/clear-completed
```

### Hủy task đang chạy
```bash
curl -X DELETE http://localhost:8080/api/v1/async/tasks/{taskId}
```

## 7. Performance Tips

1. **Tối ưu Thread Pool**: Tăng thread pool cho workload lớn
2. **Batch Processing**: Gộp nhiều requests thành batch
3. **Caching**: Cache các kết quả thường xuyên dùng
4. **Cleanup**: Xóa completed tasks định kỳ
5. **Timeout**: Set reasonable timeout cho từng loại task

## 8. Error Handling

### Common Errors

**Task Not Found**
```json
{
  "status": 404,
  "message": "Task not found"
}
```

**Task Failed**
```json
{
  "taskId": "abc-123",
  "status": "FAILED",
  "errorMessage": "Connection timeout to model service"
}
```

**Invalid Request**
```json
{
  "status": 400,
  "message": "Invalid request parameters"
}
```

## 9. Next Steps

1. Xem tài liệu chi tiết: `docs/ASYNC_API_DOCUMENTATION.md`
2. Kiểm tra code examples trong `src/test/`
3. Test với Postman collection
4. Implement trong frontend application

## 10. Support

- Documentation: `/docs/ASYNC_API_DOCUMENTATION.md`
- Health Check: `GET /api/v1/async/health`
- Stats: `GET /api/v1/async/stats`
