# API Reference

## Spring Boot REST APIs

Base URL: `http://localhost:8080/api`

### Report API

#### Get Dashboard Statistics
```http
GET /report/dashboard-stats
```

**Response**:
```json
{
  "totalCustomers": 1500,
  "totalOrders": 5000,
  "totalRevenue": 250000.00,
  "activeProducts": 350
}
```

#### Get Chart Data
```http
GET /report/chart-data?type=sales
```

**Parameters**:
- `type` (string): Chart type (sales, category, trend)

**Response**:
```json
{
  "labels": ["Jan", "Feb", "Mar"],
  "data": [10000, 15000, 20000]
}
```

---

### Marketing API

#### Get RFM Segments
```http
GET /marketing/rfm-segments
```

**Response**:
```json
[
  {
    "customerId": "C001",
    "customerName": "John Doe",
    "recency": 30,
    "frequency": 10,
    "monetary": 5000.00,
    "segment": "Champions",
    "recommendation": "Send exclusive offers"
  }
]
```

#### Get Marketing Rules
```http
GET /marketing/rules
```

**Response**:
```json
[
  {
    "antecedent": "Product A",
    "consequent": "Product B",
    "support": 0.05,
    "confidence": 0.75,
    "lift": 1.5
  }
]
```

#### Generate Marketing Rules
```http
POST /marketing/rules/generate
```

**Request Body**:
```json
{
  "minSupport": 0.01,
  "minConfidence": 0.5
}
```

**Response**:
```json
{
  "status": "success",
  "rulesGenerated": 25
}
```

---

### Policy API

#### Simulate Policy
```http
POST /policy/simulate
```

**Request Body**:
```json
{
  "orderId": "O001",
  "customerId": "C001",
  "totalAmount": 1500.00
}
```

**Response**:
```json
{
  "orderId": "O001",
  "returnRiskScore": 0.35,
  "riskLevel": "MEDIUM",
  "shouldApprove": true,
  "recommendation": "Approve with standard policy",
  "factors": {
    "customer_history": "good",
    "product_category": "electronics",
    "price_range": "medium"
  }
}
```

#### Get Return Risk
```http
GET /policy/return-risk/{orderId}
```

**Response**:
```json
{
  "orderId": "O001",
  "returnRiskScore": 0.35,
  "riskLevel": "MEDIUM"
}
```

---

### Inventory API

#### Get Anomalies
```http
GET /inventory/anomalies
```

**Response**:
```json
[
  {
    "productId": "P001",
    "productName": "Laptop",
    "anomalyScore": 0.85,
    "anomalyType": "OVERSTOCKED",
    "severity": "HIGH",
    "detectedAt": "2024-10-19T10:30:00",
    "description": "Stock level significantly above normal"
  }
]
```

#### Detect Anomalies
```http
POST /inventory/detect
```

**Request Body**:
```json
{
  "threshold": 0.7
}
```

**Response**:
```json
{
  "anomaliesDetected": 5,
  "timestamp": "2024-10-19T10:30:00"
}
```

#### Get Audit Report
```http
GET /inventory/audit-report
```

**Response**:
```json
{
  "totalProducts": 350,
  "anomaliesFound": 12,
  "overstocked": 5,
  "understocked": 7,
  "reportDate": "2024-10-19"
}
```

---

### Staff API

#### Get Recommendations
```http
GET /staff/recommendations/{customerId}
```

**Parameters**:
- `customerId` (string): Customer ID

**Response**:
```json
[
  {
    "productId": "P005",
    "productName": "Wireless Mouse",
    "confidenceScore": 0.85,
    "reason": "Frequently bought together with laptop",
    "relatedProducts": ["P001", "P003"]
  }
]
```

#### Get Performance
```http
GET /staff/performance
```

**Response**:
```json
{
  "totalRecommendations": 500,
  "acceptanceRate": 0.35,
  "averageOrderValue": 150.00
}
```

---

## Model Service (Python FastAPI)

Base URL: `http://localhost:8000`

### RFM Analysis

#### Analyze RFM
```http
POST /rfm/analyze
```

**Request Body**:
```json
{
  "customer_data": [
    {
      "customer_id": "C001",
      "customer_name": "John Doe",
      "last_purchase_date": "2024-09-15",
      "total_purchases": 10,
      "total_spent": 5000.00
    }
  ]
}
```

**Response**:
```json
{
  "segments": [
    {
      "customerId": "C001",
      "customerName": "John Doe",
      "recency": 34,
      "frequency": 10,
      "monetary": 5000.00,
      "segment": "Champions"
    }
  ]
}
```

#### Get Segments
```http
GET /rfm/segments
```

---

### Association Rules

#### Generate Rules
```http
POST /rules/generate
```

**Request Body**:
```json
{
  "transaction_data": [
    {
      "transaction_id": "T001",
      "items": ["P001", "P002", "P003"]
    }
  ],
  "min_support": 0.01,
  "min_confidence": 0.5
}
```

**Response**:
```json
{
  "rules": [
    {
      "antecedent": "P001",
      "consequent": "P002",
      "support": 0.05,
      "confidence": 0.75,
      "lift": 1.5
    }
  ]
}
```

#### List Rules
```http
GET /rules/list
```

---

### Return Risk Prediction

#### Predict Risk
```http
POST /policy/predict-risk
```

**Request Body**:
```json
{
  "order_data": {
    "order_id": "O001",
    "customer_id": "C001",
    "product_category": "Electronics",
    "price": 1500.00,
    "customer_return_history": 0.1
  }
}
```

**Response**:
```json
{
  "orderId": "O001",
  "returnRiskScore": 0.35,
  "riskLevel": "MEDIUM",
  "shouldApprove": true,
  "recommendation": "Approve with standard policy"
}
```

---

### Anomaly Detection

#### Detect Anomalies
```http
POST /inventory/detect-anomalies
```

**Request Body**:
```json
{
  "inventory_data": [
    {
      "product_id": "P001",
      "product_name": "Laptop",
      "stock_quantity": 500,
      "average_stock": 50,
      "sales_rate": 10
    }
  ]
}
```

**Response**:
```json
{
  "anomalies": [
    {
      "productId": "P001",
      "productName": "Laptop",
      "anomalyScore": 0.92,
      "anomalyType": "OVERSTOCKED",
      "severity": "HIGH"
    }
  ]
}
```

---

## Error Responses

All APIs return standard error responses:

```json
{
  "error": "Error message",
  "status": 400,
  "timestamp": "2024-10-19T10:30:00"
}
```

### HTTP Status Codes

- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Invalid request
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Authentication

Currently using basic authentication. Include credentials in requests:

```http
Authorization: Basic base64(username:password)
```

Or use session-based authentication after login through the web interface.

