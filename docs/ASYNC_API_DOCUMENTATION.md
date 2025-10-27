# Async API Documentation

## Tổng Quan

Hệ thống cung cấp các API bất đồng bộ để xử lý các tác vụ dự đoán, phân tích và gợi ý. Các API này cho phép submit task và theo dõi tiến trình xử lý.

## Cấu Hình

### Application Properties (application.yml)

```yaml
# Async Configuration
async:
  core-pool-size: 5
  max-pool-size: 20
  queue-capacity: 100
  thread-name-prefix: async-prediction-

# Model Service Configuration
model-service:
  base-url: ${MODEL_SERVICE_URL:http://localhost:8000}
  timeout: ${MODEL_SERVICE_TIMEOUT:30000}
  prediction-port: 8000
  analytics-port: 8001
  endpoints:
    sales-forecast: /api/forecast/sales
    churn-prediction: /api/predict/churn
    return-risk: /api/risk/return
    segmentation-analyze: /api/segmentation/analyze
    rfm-analysis: /api/rfm/analyze
    recommendations: /api/recommendations/generate
    market-basket: /api/market-basket/analyze
    anomaly-detect: /api/anomaly/detect
    invoice-audit: /api/audit/invoice
    policy-simulate: /api/policy/simulate
    optimal-threshold: /api/policy/optimal-threshold
    batch-evaluate: /api/policy/batch-evaluate
```

## API Endpoints

### 1. Prediction APIs

#### 1.1 Sales Forecast (Dự báo Doanh Thu)

**Submit Task**
```http
POST /api/v1/async/predictions/sales-forecast
Content-Type: application/json

{
  "predictionType": "SALES_FORECAST",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "forecastDays": 30,
  "countries": ["United Kingdom", "Germany"],
  "excludeCancellations": true,
  "callbackUrl": "http://your-app.com/callback"
}
```

**Response**
```json
{
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PENDING",
  "message": "Task submitted successfully",
  "submittedAt": "2024-10-27T10:30:00",
  "progress": 0
}
```

#### 1.2 Churn Prediction (Dự đoán Khách hàng Rời bỏ)

**Submit Task**
```http
POST /api/v1/async/predictions/churn
Content-Type: application/json

{
  "predictionType": "CHURN_PREDICTION",
  "customerIds": ["C001", "C002", "C003"],
  "startDate": "2024-01-01",
  "endDate": "2024-12-31"
}
```

#### 1.3 Return Risk Assessment (Đánh giá Rủi ro Trả hàng)

**Submit Task**
```http
POST /api/v1/async/predictions/return-risk
Content-Type: application/json

{
  "customerId": "12345",
  "orderValue": 1500.00,
  "productIds": ["P001", "P002"],
  "country": "United Kingdom"
}
```

### 2. Segmentation APIs

#### 2.1 Customer Segmentation (Phân khúc Khách hàng)

**Submit Task**
```http
POST /api/v1/async/segmentation/analyze
Content-Type: application/json

{
  "numberOfSegments": 5,
  "minSupport": 0.01,
  "minConfidence": 0.3,
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "segmentationMethod": "RFM",
  "includeRecommendations": true
}
```

#### 2.2 RFM Analysis

**Submit Task**
```http
POST /api/v1/async/segmentation/rfm
Content-Type: application/json

{
  "numberOfSegments": 5,
  "startDate": "2024-01-01",
  "endDate": "2024-12-31"
}
```

#### 2.3 Product Recommendations (Gợi ý Sản phẩm)

**Submit Task**
```http
POST /api/v1/async/segmentation/recommendations
Content-Type: application/json

{
  "numberOfSegments": 5,
  "minSupport": 0.01,
  "minConfidence": 0.5,
  "includeRecommendations": true
}
```

#### 2.4 Market Basket Analysis

**Submit Task**
```http
POST /api/v1/async/segmentation/market-basket
Content-Type: application/json

{
  "minSupport": 0.01,
  "minConfidence": 0.3
}
```

### 3. Anomaly Detection APIs

#### 3.1 Transaction Anomaly Detection

**Submit Task**
```http
POST /api/v1/async/anomaly/detect
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "zScoreThreshold": 3.0
}
```

#### 3.2 Invoice Quality Audit

**Submit Task**
```http
POST /api/v1/async/anomaly/invoice-audit
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "includeQualityScore": true
}
```

### 4. Policy Simulation APIs (Prescriptive DSS)

#### 4.1 Return Risk Policy Simulation

**Submit Task**
```http
POST /api/v1/async/policy/simulate
Content-Type: application/json

{
  "riskThreshold": 75,
  "returnProcessingCost": 50.0,
  "conversionRateImpact": 0.15,
  "simulateRange": {
    "minThreshold": 50,
    "maxThreshold": 90,
    "step": 5
  }
}
```

#### 4.2 Optimal Threshold Calculation

**Submit Task**
```http
POST /api/v1/async/policy/optimal-threshold
Content-Type: application/json

{
  "returnProcessingCost": 50.0,
  "conversionRateImpact": 0.15,
  "optimizationGoal": "MAX_PROFIT"
}
```

#### 4.3 Batch Policy Evaluation

**Submit Task**
```http
POST /api/v1/async/policy/batch-evaluate
Content-Type: application/json

{
  "orderIds": ["INV001", "INV002", "INV003"],
  "riskThreshold": 75
}
```

### 5. Task Management APIs

#### 5.1 Get Task Status

**Request**
```http
GET /api/v1/async/tasks/{taskId}
```

**Response**
```json
{
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PROCESSING",
  "message": "Task is being processed",
  "submittedAt": "2024-10-27T10:30:00",
  "progress": 50
}
```

**Response (Completed)**
```json
{
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "message": "Task completed successfully",
  "submittedAt": "2024-10-27T10:30:00",
  "completedAt": "2024-10-27T10:35:00",
  "progress": 100,
  "result": {
    "forecast": [...],
    "metrics": {...}
  }
}
```

#### 5.2 Get All Tasks

**Request**
```http
GET /api/v1/async/tasks
GET /api/v1/async/tasks?status=PENDING,PROCESSING
```

**Response**
```json
{
  "550e8400-e29b-41d4-a716-446655440000": {
    "taskId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "COMPLETED",
    ...
  },
  "660e8400-e29b-41d4-a716-446655440001": {
    "taskId": "660e8400-e29b-41d4-a716-446655440001",
    "status": "PROCESSING",
    ...
  }
}
```

#### 5.3 Cancel Task

**Request**
```http
DELETE /api/v1/async/tasks/{taskId}
```

**Response**
```json
{
  "status": "cancelled",
  "message": "Task cancelled successfully",
  "taskId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### 5.4 Remove Task

**Request**
```http
DELETE /api/v1/async/tasks/{taskId}/remove
```

#### 5.5 Clear Completed Tasks

**Request**
```http
POST /api/v1/async/tasks/clear-completed
```

**Response**
```json
{
  "status": "success",
  "message": "Completed tasks cleared",
  "tasksRemoved": 15,
  "tasksRemaining": 5
}
```

#### 5.6 Get Task Statistics

**Request**
```http
GET /api/v1/async/stats
```

**Response**
```json
{
  "total": 20,
  "pending": 3,
  "processing": 2,
  "completed": 12,
  "failed": 2,
  "cancelled": 1,
  "active": 5
}
```

#### 5.7 Health Check

**Request**
```http
GET /api/v1/async/health
```

**Response**
```json
{
  "status": "UP",
  "service": "Async Task Manager",
  "timestamp": "2024-10-27T10:30:00",
  "activeTasks": 5,
  "totalTasks": 20
}
```

## Task Status Flow

```
PENDING → PROCESSING → COMPLETED
                    → FAILED
                    → CANCELLED
```

## Task Lifecycle

1. **Submit Task**: POST request tới endpoint tương ứng
2. **Get Task ID**: Nhận taskId trong response
3. **Poll Status**: Gọi GET `/api/v1/async/tasks/{taskId}` để kiểm tra tiến trình
4. **Get Result**: Khi status = "COMPLETED", lấy kết quả từ field `result`
5. **Clean Up**: (Optional) Xóa task đã hoàn thành

## Error Handling

**Failed Task Response**
```json
{
  "taskId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "FAILED",
  "message": "Task failed",
  "errorMessage": "Connection timeout to model service",
  "completedAt": "2024-10-27T10:35:00"
}
```

## Best Practices

1. **Polling Interval**: Poll status mỗi 2-5 giây để tránh overload
2. **Timeout Handling**: Set timeout client-side (recommend: 5 phút)
3. **Clean Up**: Xóa các task đã hoàn thành để tiết kiệm memory
4. **Callback URL**: Sử dụng callback URL thay vì polling nếu có thể
5. **Error Retry**: Implement retry logic cho failed tasks

## Monitoring

Sử dụng các endpoint sau để monitor:
- `/api/v1/async/health` - Health check
- `/api/v1/async/stats` - Task statistics
- `/actuator/metrics` - Spring Boot Actuator metrics

## Architecture

```
Client
  ↓
Async API Controller
  ↓
Async Task Manager (Task Tracking)
  ↓
Thread Pool Executor (Async Processing)
  ↓
RestTemplate (Call Model Service)
  ↓
Model Service (Python/Flask)
```

## Configuration Tips

### Tăng Thread Pool nếu có nhiều requests:
```yaml
async:
  core-pool-size: 10
  max-pool-size: 50
  queue-capacity: 200
```

### Tăng timeout cho tasks lớn:
```yaml
model-service:
  timeout: 60000  # 60 seconds
```

### Sử dụng nhiều ports cho load balancing:
```yaml
model-service:
  prediction-port: 8000
  analytics-port: 8001
```
