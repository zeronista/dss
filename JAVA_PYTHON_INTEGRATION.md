# 🎯 Quick Start: Java + Python Integration

## ✅ Đã tích hợp xong!

Dự án của bạn đã được tích hợp thành công giữa **Java Spring Boot** và **Python FastAPI** để xử lý các tính năng ML/AI.

## 📂 Cấu trúc Dự án

```
dss/
├── src/main/java/com/g5/dss/
│   ├── api/
│   │   └── PythonMLGatewayController.java  ✨ NEW - REST API Gateway
│   ├── service/
│   │   └── PythonMLService.java            ✨ NEW - Python integration
│   └── controller/
│       └── MLAnalyticsController.java      ✨ NEW - Web UI for ML
│
├── api/                                     ✨ NEW - Python ML Service
│   ├── main.py                             - FastAPI app
│   ├── services.py                         - Business logic
│   ├── models.py                           - Data models
│   └── requirements.txt                    - Dependencies
│
├── INTEGRATION_GUIDE.md                     ✨ NEW - Integration docs
└── test-integration.ps1                     ✨ NEW - Test script
```

## 🚀 Cách chạy (2 bước)

### Bước 1: Khởi động Python ML Service

```powershell
# Terminal 1
cd api
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

✅ Kiểm tra: http://localhost:8000/docs

### Bước 2: Khởi động Java Spring Boot

```powershell
# Terminal 2
mvn spring-boot:run
```

✅ Kiểm tra: http://localhost:8080

## 🧪 Test Integration

```powershell
# Chạy test script
.\test-integration.ps1
```

Script sẽ tự động test:
- ✅ Python service health
- ✅ Java service health
- ✅ Integration connectivity
- ✅ File upload
- ✅ Forecast API
- ✅ Segmentation API

## 📡 Sử dụng API

### Từ Java Code

```java
@RestController
@RequiredArgsConstructor
public class MyController {
    private final PythonMLService pythonMLService;
    
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestParam MultipartFile file) {
        // Upload to Python
        Map<String, Object> result = pythonMLService.uploadData(file);
        String sessionId = (String) result.get("session_id");
        
        // Get forecast
        Map<String, Object> forecast = pythonMLService.forecastRevenue(
            Map.of("session_id", sessionId, "periods", 3)
        );
        
        return ResponseEntity.ok(forecast);
    }
}
```

### Từ JavaScript/Frontend

```javascript
// Upload file
const formData = new FormData();
formData.append('file', fileInput.files[0]);

const upload = await fetch('/api/ml/upload', {
    method: 'POST',
    body: formData
});
const { session_id } = await upload.json();

// Get forecast
const forecast = await fetch('/api/ml/forecast', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        session_id,
        periods: 3
    })
});
const data = await forecast.json();
```

### Từ cURL/Postman

```bash
# Upload file
curl -X POST http://localhost:8080/api/ml/upload \
  -F "file=@online_retail.csv"

# Get forecast
curl -X POST http://localhost:8080/api/ml/forecast \
  -H "Content-Type: application/json" \
  -d '{"session_id":"abc123","periods":3}'
```

## 🎨 Web UI Pages

Truy cập các trang web:

- **ML Dashboard**: http://localhost:8080/ml
- **Revenue Forecast**: http://localhost:8080/ml/forecast
- **Customer Segmentation**: http://localhost:8080/ml/segmentation
- **Market Basket**: http://localhost:8080/ml/market-basket
- **Churn Prediction**: http://localhost:8080/ml/churn
- **RFM Analysis**: http://localhost:8080/ml/rfm

## 📊 Tính năng ML/AI

| Feature | Endpoint | Description |
|---------|----------|-------------|
| 📈 Forecasting | `/api/ml/forecast` | Dự báo doanh thu (SARIMAX) |
| 👥 Segmentation | `/api/ml/segmentation` | Phân khúc khách hàng (K-Means) |
| 🛍️ Market Basket | `/api/ml/market-basket` | Gợi ý sản phẩm bán kèm (Apriori) |
| ⚠️ Churn | `/api/ml/churn` | Dự đoán khách hàng rời bỏ |
| 💎 RFM | `/api/ml/rfm` | Phân tích RFM |

## 🔧 Configuration

File: `src/main/resources/application.yml`

```yaml
python:
  ml:
    service:
      url: ${PYTHON_ML_URL:http://localhost:8000}
      timeout: 30000
      enabled: true
```

## 📚 Documentation

- **Integration Guide**: [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)
- **Python API Docs**: http://localhost:8000/docs
- **Python API README**: [api/README.md](api/README.md)
- **Python Quick Start**: [api/QUICKSTART.md](api/QUICKSTART.md)

## 🎯 Workflow Example

```
1. User uploads CSV via Java UI
   ↓
2. Java → POST /api/ml/upload → Python
   ← session_id: "abc123"
   ↓
3. User clicks "Forecast"
   ↓
4. Java → POST /api/ml/forecast → Python
   ← forecast_data: [...]
   ↓
5. Java renders chart in browser
```

## 💡 Tips

1. **Always check health first**: `GET /api/ml/health`
2. **Save session_id**: Tránh upload lại file nhiều lần
3. **Error handling**: Check Python logs nếu có lỗi
4. **Performance**: Large files (~500k rows) mất ~10-30s để phân tích

## 🐛 Troubleshooting

### Python not reachable

```
Error: Connection refused
```

**Solution:**
```powershell
cd api
uvicorn main:app --reload --port 8000
```

### Port already in use

```powershell
# Check port 8000
netstat -ano | findstr :8000

# Kill process
taskkill /PID <PID> /F
```

## 🎓 Next Steps

1. ✅ Tạo Thymeleaf templates cho ML pages
2. ✅ Thêm charts (Chart.js/Plotly)
3. ✅ Add authentication
4. ✅ Deploy to production
5. ✅ Add caching với Redis

## 📞 Support

Nếu gặp vấn đề:
1. Check logs: Python terminal & Java console
2. Run test script: `.\test-integration.ps1`
3. Check health: http://localhost:8080/api/ml/health
4. Review: [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)

---

**🎉 Chúc mừng! Hệ thống đã sẵn sàng sử dụng!**
