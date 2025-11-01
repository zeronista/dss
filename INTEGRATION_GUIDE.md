# Integration Guide: Java Spring Boot + Python FastAPI

## 🎯 Tổng quan Kiến trúc

```
┌─────────────────────────────────────────────────────────┐
│                    Client (Browser)                      │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│          Java Spring Boot (Port 8080)                    │
│  ┌────────────────────────────────────────────────┐    │
│  │  - Web UI (Thymeleaf)                          │    │
│  │  - Authentication & Authorization              │    │
│  │  - CRUD Operations (MySQL/MongoDB)             │    │
│  │  - Business Logic                              │    │
│  │  - REST API Gateway (/api/ml/*)                │    │
│  └────────────────┬───────────────────────────────┘    │
└───────────────────┼──────────────────────────────────────┘
                    │ HTTP REST Calls
                    ▼
┌─────────────────────────────────────────────────────────┐
│          Python FastAPI (Port 8000)                      │
│  ┌────────────────────────────────────────────────┐    │
│  │  - Revenue Forecasting (SARIMAX)               │    │
│  │  - Customer Segmentation (K-Means)             │    │
│  │  - RFM Analysis                                │    │
│  │  - Market Basket Analysis (Apriori)            │    │
│  │  - Churn Prediction                            │    │
│  └────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

## 📦 Đã tạo các file

### Java Side (Spring Boot)

1. **`PythonMLGatewayController.java`** - REST API Gateway
   - Endpoints: `/api/ml/*`
   - Proxy requests từ Java sang Python
   - 10 endpoints chính

2. **`PythonMLService.java`** - Service Layer
   - Xử lý HTTP communication với Python
   - RestTemplate để gọi Python API
   - Error handling & logging

3. **`MLAnalyticsController.java`** - Web UI Controller
   - Pages: `/ml/forecast`, `/ml/segmentation`, etc.
   - Render Thymeleaf templates
   - Check Python service health

4. **`application.yml`** - Configuration
   - Python service URL: `python.ml.service.url`
   - Default: `http://localhost:8000`

### Python Side (FastAPI)

Đã tạo đầy đủ trong folder `api/`:
- `main.py` - FastAPI app với tất cả endpoints
- `services.py` - Business logic (6 services)
- `models.py` - Pydantic validation models
- `requirements.txt` - Dependencies

## 🚀 Cách chạy hệ thống

### Bước 1: Khởi động Python ML Service

```powershell
# Terminal 1: Chạy Python API
cd api
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

Kiểm tra: http://localhost:8000/docs

### Bước 2: Khởi động Java Spring Boot

```powershell
# Terminal 2: Chạy Spring Boot
mvn spring-boot:run
```

Hoặc trong IDE: Run `DssApplication.java`

Kiểm tra: http://localhost:8080

### Bước 3: Test Integration

```powershell
# Check Python health từ Java
curl http://localhost:8080/api/ml/health
```

## 📡 API Endpoints

### Java Gateway Endpoints (Port 8080)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/ml/health` | Check Python service health |
| POST | `/api/ml/upload` | Upload CSV to Python |
| POST | `/api/ml/overview` | Get overview metrics |
| POST | `/api/ml/forecast` | Revenue forecasting |
| POST | `/api/ml/rfm` | RFM analysis |
| POST | `/api/ml/segmentation` | Customer segmentation |
| POST | `/api/ml/market-basket` | Market basket analysis |
| POST | `/api/ml/churn` | Churn prediction |
| GET | `/api/ml/download/rfm/{sessionId}` | Download RFM CSV |
| GET | `/api/ml/download/segments/{sessionId}` | Download segments CSV |

### Web UI Pages (Port 8080)

| URL | Description |
|-----|-------------|
| `/ml` | ML Analytics Dashboard |
| `/ml/forecast` | Revenue Forecasting Page |
| `/ml/segmentation` | Customer Segmentation Page |
| `/ml/market-basket` | Market Basket Analysis Page |
| `/ml/churn` | Churn Prediction Page |
| `/ml/rfm` | RFM Analysis Page |

## 💡 Ví dụ Sử dụng

### 1. Java Code - Gọi Python từ Controller khác

```java
@RestController
@RequiredArgsConstructor
public class MyController {
    
    private final PythonMLService pythonMLService;
    
    @PostMapping("/my-endpoint")
    public ResponseEntity<?> myEndpoint() {
        // Upload data
        Map<String, Object> uploadResult = pythonMLService.uploadData(file);
        String sessionId = (String) uploadResult.get("session_id");
        
        // Get forecast
        Map<String, Object> forecastRequest = Map.of(
            "session_id", sessionId,
            "periods", 3
        );
        Map<String, Object> forecast = pythonMLService.forecastRevenue(forecastRequest);
        
        return ResponseEntity.ok(forecast);
    }
}
```

### 2. JavaScript - Gọi từ Frontend

```javascript
// Upload CSV file
const formData = new FormData();
formData.append('file', fileInput.files[0]);

const uploadResponse = await fetch('/api/ml/upload', {
    method: 'POST',
    body: formData
});
const { session_id } = await uploadResponse.json();

// Get forecast
const forecastResponse = await fetch('/api/ml/forecast', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        session_id,
        periods: 3,
        confidence_level: 0.90
    })
});
const forecastData = await forecastResponse.json();

// Display results
console.log(forecastData.forecast_data);
```

### 3. Thymeleaf Template Example

```html
<!-- ml_forecast.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${pageTitle}">Dự báo Doanh thu</title>
</head>
<body>
    <div class="container">
        <h1 th:text="${pageTitle}">Dự báo Doanh thu</h1>
        
        <!-- Python Service Status -->
        <div th:if="${pythonHealth.python_status == 'healthy'}" 
             class="alert alert-success">
            ✅ Python ML Service: Connected
        </div>
        <div th:unless="${pythonHealth.python_status == 'healthy'}" 
             class="alert alert-danger">
            ❌ Python ML Service: Disconnected
        </div>
        
        <!-- Upload Form -->
        <form id="uploadForm" enctype="multipart/form-data">
            <input type="file" name="file" accept=".csv" required>
            <button type="submit">Upload & Analyze</button>
        </form>
        
        <!-- Results -->
        <div id="results"></div>
    </div>
    
    <script>
        document.getElementById('uploadForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const formData = new FormData(e.target);
            
            // Upload
            const uploadRes = await fetch('/api/ml/upload', {
                method: 'POST',
                body: formData
            });
            const { session_id } = await uploadRes.json();
            
            // Forecast
            const forecastRes = await fetch('/api/ml/forecast', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ session_id, periods: 3 })
            });
            const data = await forecastRes.json();
            
            // Display
            document.getElementById('results').innerHTML = 
                JSON.stringify(data, null, 2);
        });
    </script>
</body>
</html>
```

## 🔧 Configuration

### application.yml

```yaml
python:
  ml:
    service:
      url: ${PYTHON_ML_URL:http://localhost:8000}
      timeout: 30000
      enabled: true
```

### Environment Variables

```bash
# Development
export PYTHON_ML_URL=http://localhost:8000

# Production
export PYTHON_ML_URL=http://python-ml-service:8000
```

## 🐳 Docker Deployment

### docker-compose.yml

```yaml
version: '3.8'

services:
  java-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - PYTHON_ML_URL=http://python-ml:8000
    depends_on:
      - python-ml
      
  python-ml:
    build: ./api
    ports:
      - "8000:8000"
    volumes:
      - ./data:/app/data
```

## 📊 Data Flow Example

### Complete Workflow

```
1. User uploads CSV via Java UI
   ↓
2. Java Controller receives file
   ↓
3. PythonMLService.uploadData(file)
   → POST http://localhost:8000/api/upload
   ← Response: { session_id: "abc123" }
   ↓
4. User requests forecast
   ↓
5. PythonMLService.forecastRevenue({ session_id: "abc123" })
   → POST http://localhost:8000/api/forecast
   ← Response: { forecast_data: [...] }
   ↓
6. Java returns data to frontend
   ↓
7. Frontend renders charts
```

## 🧪 Testing

### Test Python Service

```powershell
# Check if Python is running
curl http://localhost:8000/health

# Test upload
curl -F "file=@online_retail.csv" http://localhost:8000/api/upload
```

### Test Java Integration

```powershell
# Check Python health via Java
curl http://localhost:8080/api/ml/health

# Test end-to-end
curl -F "file=@online_retail.csv" http://localhost:8080/api/ml/upload
```

## ⚠️ Troubleshooting

### Python service unreachable

```
Error: Connection refused to http://localhost:8000
```

**Solution:**
1. Check if Python is running: `curl http://localhost:8000/health`
2. Check port: `netstat -ano | findstr :8000`
3. Update `application.yml` with correct URL

### CORS Issues

Add to Python `main.py`:

```python
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
```

## 🎓 Best Practices

1. **Error Handling**: Always check Python service health before making calls
2. **Timeouts**: Set appropriate timeouts for long-running ML operations
3. **Caching**: Cache session IDs to avoid re-uploading data
4. **Async**: Use async processing for long operations (forecasting)
5. **Security**: Add authentication to Python API in production

## 📚 Next Steps

1. ✅ Tạo Thymeleaf templates cho ML pages
2. ✅ Add loading indicators cho long operations
3. ✅ Implement caching với Redis
4. ✅ Add authentication cho Python API
5. ✅ Deploy to production

## 🤝 Support

- Java logs: `logs/spring-boot.log`
- Python logs: Console output
- Health check: `/api/ml/health`
