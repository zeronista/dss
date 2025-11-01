# Online Retail DSS API

REST API cho hệ thống hỗ trợ quyết định bán lẻ trực tuyến, cung cấp các chức năng phân tích dữ liệu, dự báo, phân khúc khách hàng và phân tích giỏ hàng.

## 📋 Tính năng

- **📊 Tổng quan dữ liệu**: KPI metrics, top products, top customers, revenue trends
- **📈 Dự báo doanh thu**: SARIMAX forecasting với confidence intervals
- **👥 RFM Analysis**: Phân tích Recency, Frequency, Monetary cho khách hàng
- **🎯 Customer Segmentation**: K-Means clustering để phân khúc khách hàng
- **🛍️ Market Basket Analysis**: Apriori algorithm để tìm sản phẩm bán kèm
- **⚠️ Churn Prediction**: Xác định khách hàng có nguy cơ rời bỏ

## 🚀 Cài đặt

### 1. Cài đặt dependencies

```bash
cd api
pip install -r requirements.txt
```

### 2. Chạy API server

```bash
# Development mode
uvicorn main:app --reload --host 0.0.0.0 --port 8000

# Production mode
uvicorn main:app --host 0.0.0.0 --port 8000 --workers 4
```

### 3. Truy cập API Documentation

- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## 📖 API Endpoints

### Health Check

```http
GET /health
```

**Response:**
```json
{
  "status": "healthy",
  "timestamp": "2025-11-01T10:00:00",
  "data_loaded": true
}
```

### Upload Data

```http
POST /api/upload
Content-Type: multipart/form-data

file: online_retail.csv
```

**Response:**
```json
{
  "session_id": "session_1730448000.123",
  "rows": 541909,
  "columns": 8,
  "date_range": {
    "min": "2010-12-01T08:26:00",
    "max": "2011-12-09T12:50:00"
  },
  "stats": {
    "total_revenue": 9747748.18,
    "total_transactions": 25900,
    "total_customers": 4372
  }
}
```

### Overview Metrics

```http
POST /api/overview
Content-Type: application/json

{
  "session_id": "session_1730448000.123",
  "start_date": "2010-12-01",
  "end_date": "2011-12-09"
}
```

**Response:**
```json
{
  "total_revenue": 9747748.18,
  "total_customers": 4372,
  "total_orders": 25900,
  "avg_order_value": 376.44,
  "top_products": [...],
  "top_customers": [...],
  "monthly_revenue": [...],
  "revenue_by_country": [...],
  "insights": [
    "Sản phẩm chủ lực: WHITE HANGING HEART chiếm 2.3% tổng số lượng bán ra",
    "Thị trường lớn nhất: United Kingdom đóng góp 82.1% doanh thu"
  ]
}
```

### Revenue Forecast

```http
POST /api/forecast
Content-Type: application/json

{
  "session_id": "session_1730448000.123",
  "periods": 3,
  "confidence_level": 0.90
}
```

**Response:**
```json
{
  "historical_data": [
    {"date": "2010-12-01", "revenue": 748943.52},
    {"date": "2011-01-01", "revenue": 560000.21}
  ],
  "forecast_data": [
    {
      "date": "2012-01-01",
      "predicted_value": 850000.50,
      "lower_bound": 750000.00,
      "upper_bound": 950000.00
    }
  ],
  "model_info": {
    "model_type": "SARIMAX",
    "order": "(1,1,1)",
    "seasonal_order": "(1,0,1,12)"
  },
  "insights": [
    "Dự báo tăng trưởng mạnh (12.5%): Hãy tăng tồn kho và chuẩn bị nguồn lực"
  ]
}
```

### RFM Analysis

```http
POST /api/rfm
Content-Type: application/json

{
  "session_id": "session_1730448000.123",
  "reference_date": "2011-12-09"
}
```

**Response:**
```json
{
  "customers": [
    {
      "CustomerID": 12345,
      "Recency": 15,
      "Frequency": 25,
      "Monetary": 5420.50,
      "RFM_Score": "555"
    }
  ],
  "summary_stats": {
    "total_customers": 4372,
    "avg_recency": 92.5,
    "avg_frequency": 5.9,
    "avg_monetary": 2230.15
  }
}
```

### Customer Segmentation

```http
POST /api/segmentation
Content-Type: application/json

{
  "session_id": "session_1730448000.123",
  "n_clusters": 4,
  "random_state": 42
}
```

**Response:**
```json
{
  "segments": [...],
  "segment_summary": [
    {
      "segment_id": 0,
      "segment_name": "Champions",
      "customer_count": 523,
      "total_value": 2847593.45,
      "avg_recency": 18.5,
      "avg_frequency": 15.2,
      "avg_monetary": 5445.67,
      "characteristics": "Khách hàng VIP với tần suất mua cao...",
      "marketing_actions": [
        "Ưu đãi VIP và early access",
        "Chương trình giới thiệu bạn bè"
      ]
    }
  ],
  "silhouette_score": 0.52
}
```

### Market Basket Analysis

```http
POST /api/market-basket
Content-Type: application/json

{
  "session_id": "session_1730448000.123",
  "min_support": 0.01,
  "min_confidence": 0.2,
  "max_rules": 10
}
```

**Response:**
```json
{
  "rules": [
    {
      "antecedents": ["WHITE HANGING HEART"],
      "consequents": ["RED RETROSPOT PLATE"],
      "support": 0.025,
      "confidence": 0.65,
      "lift": 2.3
    }
  ],
  "top_rule": {...},
  "insights": [
    "Bundle đề xuất hàng đầu: WHITE HANGING HEART -> RED RETROSPOT PLATE (Confidence: 65.0%, Lift: 2.30)"
  ],
  "metrics": {
    "total_rules": 10,
    "avg_confidence": 0.48,
    "avg_lift": 1.85
  }
}
```

### Churn Prediction

```http
POST /api/churn
Content-Type: application/json

{
  "session_id": "session_1730448000.123",
  "recency_threshold_pct": 75,
  "frequency_threshold_pct": 25
}
```

**Response:**
```json
{
  "at_risk_customers": [
    {
      "CustomerID": 12345,
      "Recency": 180,
      "Frequency": 2,
      "Monetary": 450.00,
      "ChurnRiskScore": 75.5,
      "RecommendedActions": [
        "Gửi email cá nhân hóa 'Chúng tôi nhớ bạn'",
        "Mã giảm giá 15% có thời hạn"
      ]
    }
  ],
  "risk_summary": {
    "total_customers": 4372,
    "at_risk_count": 873,
    "risk_percentage": 19.97,
    "risk_level": "Medium"
  },
  "recommendations": [
    "⚠️ Cảnh báo: 19.97% khách hàng có nguy cơ rời bỏ",
    "Lên kế hoạch chiến dịch re-engagement trong 14 ngày"
  ],
  "potential_value_at_risk": 487230.50
}
```

### Download CSV

```http
GET /api/download/rfm/{session_id}
GET /api/download/segments/{session_id}?n_clusters=4
```

## 🔧 Configuration

### Environment Variables

Tạo file `.env`:

```env
# API Settings
API_HOST=0.0.0.0
API_PORT=8000
API_WORKERS=4

# CORS Settings
CORS_ORIGINS=["http://localhost:3000", "http://localhost:8080"]

# Data Settings
MAX_FILE_SIZE=50MB
SESSION_TIMEOUT=3600

# Optional: Database
# DATABASE_URL=mysql://user:pass@localhost:3306/retail_dss
# MONGODB_URL=mongodb://localhost:27017/retail_dss

# Optional: Redis Cache
# REDIS_URL=redis://localhost:6379/0
```

## 📊 Data Format

### Input CSV Format

File CSV cần có các cột sau:

| Column | Type | Description | Example |
|--------|------|-------------|---------|
| InvoiceNo | string | Mã hóa đơn | 536365 |
| StockCode | string | Mã sản phẩm | 85123A |
| Description | string | Tên sản phẩm | WHITE HANGING HEART |
| Quantity | int | Số lượng | 6 |
| InvoiceDate | datetime | Ngày mua | 12/1/2010 8:26 |
| UnitPrice | float | Đơn giá | 2.55 |
| CustomerID | int | Mã khách hàng | 17850 |
| Country | string | Quốc gia | United Kingdom |

## 🧪 Testing

```bash
# Run tests
pytest

# Run with coverage
pytest --cov=. --cov-report=html

# Test specific endpoint
pytest tests/test_forecast.py -v
```

## 📝 Examples

### Python Client Example

```python
import requests
import pandas as pd

# Upload data
files = {'file': open('online_retail.csv', 'rb')}
response = requests.post('http://localhost:8000/api/upload', files=files)
session_id = response.json()['session_id']

# Get overview
overview = requests.post('http://localhost:8000/api/overview', json={
    'session_id': session_id,
    'start_date': '2010-12-01',
    'end_date': '2011-12-09'
})
print(overview.json())

# Forecast
forecast = requests.post('http://localhost:8000/api/forecast', json={
    'session_id': session_id,
    'periods': 3
})
print(forecast.json())

# Segmentation
segments = requests.post('http://localhost:8000/api/segmentation', json={
    'session_id': session_id,
    'n_clusters': 5
})
print(segments.json())
```

### JavaScript/TypeScript Example

```typescript
// Upload data
const formData = new FormData();
formData.append('file', fileInput.files[0]);

const uploadResponse = await fetch('http://localhost:8000/api/upload', {
  method: 'POST',
  body: formData
});
const { session_id } = await uploadResponse.json();

// Get segmentation
const segmentResponse = await fetch('http://localhost:8000/api/segmentation', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    session_id,
    n_clusters: 4
  })
});
const segments = await segmentResponse.json();
console.log(segments);
```

## 🏗️ Architecture

```
api/
├── main.py              # FastAPI app & endpoints
├── models.py            # Pydantic models (request/response)
├── services.py          # Business logic services
├── requirements.txt     # Python dependencies
└── README.md           # This file
```

### Service Layer

- **DataService**: Data loading, cleaning, filtering
- **ForecastService**: SARIMAX revenue forecasting
- **RFMService**: RFM analysis calculations
- **SegmentationService**: K-Means customer clustering
- **MarketBasketService**: Apriori association rules
- **ChurnService**: Churn risk identification

## 🚀 Deployment

### Docker

```dockerfile
FROM python:3.10-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
```

```bash
docker build -t retail-dss-api .
docker run -p 8000:8000 retail-dss-api
```

### Docker Compose

```yaml
version: '3.8'
services:
  api:
    build: .
    ports:
      - "8000:8000"
    environment:
      - API_HOST=0.0.0.0
      - API_PORT=8000
    volumes:
      - ./data:/app/data
```

## 📚 Documentation

- **API Docs**: http://localhost:8000/docs
- **Alternative Docs**: http://localhost:8000/redoc
- **OpenAPI Schema**: http://localhost:8000/openapi.json

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

## 📄 License

MIT License

## 👥 Authors

- GitHub Copilot
- G5_GP1 Team

## 🙏 Acknowledgments

- FastAPI framework
- Scikit-learn
- Statsmodels
- MLxtend
