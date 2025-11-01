# Quick Start Guide - Online Retail DSS API

## 🚀 Hướng dẫn Nhanh

### Bước 1: Cài đặt Dependencies

```powershell
# Di chuyển vào thư mục API
cd api

# Cài đặt packages
pip install -r requirements.txt
```

### Bước 2: Khởi động API Server

```powershell
# Chạy server (development mode)
uvicorn main:app --reload
```

Server sẽ chạy tại: `http://localhost:8000`

### Bước 3: Kiểm tra API

Mở trình duyệt và truy cập:
- **Swagger UI**: http://localhost:8000/docs
- **Health Check**: http://localhost:8000/health

### Bước 4: Upload Dữ liệu

**Cách 1: Sử dụng Swagger UI**

1. Mở http://localhost:8000/docs
2. Chọn endpoint `POST /api/upload`
3. Click "Try it out"
4. Chọn file `online_retail.csv`
5. Click "Execute"
6. Copy `session_id` từ response

**Cách 2: Sử dụng Python**

```python
import requests

# Upload file
with open('online_retail.csv', 'rb') as f:
    response = requests.post(
        'http://localhost:8000/api/upload',
        files={'file': f}
    )
    
session_id = response.json()['session_id']
print(f"Session ID: {session_id}")
```

**Cách 3: Sử dụng cURL**

```powershell
curl -X POST "http://localhost:8000/api/upload" `
  -F "file=@online_retail.csv"
```

### Bước 5: Gọi các API Endpoints

#### 1. Xem Tổng quan (Overview)

```python
import requests

overview = requests.post(
    'http://localhost:8000/api/overview',
    json={
        'session_id': 'your_session_id_here',
        'start_date': '2010-12-01',
        'end_date': '2011-12-09'
    }
)

print(overview.json())
```

#### 2. Dự báo Doanh thu (Forecast)

```python
forecast = requests.post(
    'http://localhost:8000/api/forecast',
    json={
        'session_id': 'your_session_id_here',
        'periods': 3,
        'confidence_level': 0.90
    }
)

for fc in forecast.json()['forecast_data']:
    print(f"{fc['date']}: {fc['predicted_value']:,.2f}")
```

#### 3. Phân khúc Khách hàng (Segmentation)

```python
segments = requests.post(
    'http://localhost:8000/api/segmentation',
    json={
        'session_id': 'your_session_id_here',
        'n_clusters': 4
    }
)

for seg in segments.json()['segment_summary']:
    print(f"{seg['segment_name']}: {seg['customer_count']} customers")
```

#### 4. Phân tích Giỏ hàng (Market Basket)

```python
basket = requests.post(
    'http://localhost:8000/api/market-basket',
    json={
        'session_id': 'your_session_id_here',
        'min_support': 0.01,
        'min_confidence': 0.3
    }
)

top_rule = basket.json()['top_rule']
print(f"Bundle: {top_rule['antecedents']} -> {top_rule['consequents']}")
```

#### 5. Dự đoán Rời bỏ (Churn)

```python
churn = requests.post(
    'http://localhost:8000/api/churn',
    json={
        'session_id': 'your_session_id_here',
        'recency_threshold_pct': 75,
        'frequency_threshold_pct': 25
    }
)

summary = churn.json()['risk_summary']
print(f"At-risk: {summary['at_risk_count']} customers ({summary['risk_percentage']:.1f}%)")
```

### Bước 6: Download Kết quả

```python
# Download RFM analysis
rfm_url = f"http://localhost:8000/api/download/rfm/{session_id}"
response = requests.get(rfm_url)
with open('rfm_results.csv', 'wb') as f:
    f.write(response.content)

# Download segmentation
seg_url = f"http://localhost:8000/api/download/segments/{session_id}?n_clusters=4"
response = requests.get(seg_url)
with open('segments.csv', 'wb') as f:
    f.write(response.content)
```

## 📝 Chạy Script Ví dụ Hoàn chỉnh

```powershell
# Chạy file example
python example_usage.py
```

## 🐳 Chạy với Docker

```powershell
# Build image
docker build -t retail-dss-api .

# Run container
docker run -p 8000:8000 retail-dss-api
```

Hoặc dùng Docker Compose:

```powershell
docker-compose up -d
```

## 🧪 Chạy Tests

```powershell
# Install test dependencies
pip install pytest pytest-asyncio httpx

# Run tests
pytest test_api.py -v

# Run with coverage
pytest test_api.py --cov=. --cov-report=html
```

## 🔧 Troubleshooting

### Lỗi: Module not found

```powershell
pip install -r requirements.txt --upgrade
```

### Lỗi: Port 8000 đã được sử dụng

```powershell
# Chạy trên port khác
uvicorn main:app --reload --port 8080
```

### Lỗi: File not found khi upload

Đảm bảo file `online_retail.csv` có các cột:
- InvoiceNo
- StockCode
- Description
- Quantity
- InvoiceDate
- UnitPrice
- CustomerID
- Country

## 📚 Tài liệu API

- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc
- **OpenAPI JSON**: http://localhost:8000/openapi.json

## 💡 Tips

1. **Session Management**: Session ID sẽ hết hạn sau 1 giờ (default)
2. **File Size**: Upload tối đa 50MB
3. **Performance**: Với file lớn, các phân tích có thể mất vài giây
4. **CORS**: Default cho phép tất cả origins - cấu hình trong `config.py` cho production

## 🎯 Next Steps

1. Tích hợp với frontend (React/Vue/Angular)
2. Thêm authentication (JWT tokens)
3. Kết nối database để lưu trữ persistent
4. Deploy lên cloud (AWS/Azure/GCP)
5. Thêm caching với Redis
6. Monitoring và logging

## 📞 Hỗ trợ

Nếu gặp vấn đề, kiểm tra:
1. Log của API server
2. Response error messages
3. Network tab trong browser DevTools
4. API documentation tại /docs
