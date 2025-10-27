# 📊 Hướng Dẫn Sử Dụng Dashboard

## ✅ Đã Hoàn Thành

Tôi đã tạo một dashboard hoàn chỉnh để hiển thị thông tin từ MySQL database của bạn tại địa chỉ: **http://localhost:8080/dashboard**

### 📁 Files Đã Tạo/Cập Nhật:

1. **Backend Controllers:**
   - `DashboardController.java` - Controller chính cho trang dashboard
   - `ReportApi.java` - REST API cung cấp dữ liệu cho charts và statistics

2. **Frontend Template:**
   - `dashboard.html` - Giao diện dashboard với gradient cards và interactive charts

## 🚀 Cách Sử Dụng

### 1. Chạy Ứng Dụng

```powershell
# Từ thư mục dss, chạy lệnh:
mvnw spring-boot:run
```

Hoặc nếu đã build:
```powershell
java -jar target/dss-0.0.1-SNAPSHOT.jar
```

### 2. Truy Cập Dashboard

Mở trình duyệt và truy cập:
```
http://localhost:8080/dashboard
```

## 📊 Tính Năng Dashboard

### 1. **Statistics Cards (4 thẻ thống kê)**

- 👥 **Unique Customers** - Số lượng khách hàng unique
- 📝 **Total Orders** - Tổng số đơn hàng (records trong database)
- 💰 **Total Revenue** - Tổng doanh thu (tính từ các quốc gia)
- 📦 **Active Products** - Số lượng sản phẩm active

Mỗi thẻ có gradient màu đẹp mắt và hiệu ứng hover!

### 2. **Interactive Charts (2 biểu đồ)**

- 🌍 **Top Countries by Sales** - Top 10 quốc gia theo doanh số (Bar Chart)
- 📦 **Top Products by Quantity** - Top 10 sản phẩm bán chạy (Horizontal Bar Chart)

Sử dụng **Chart.js** để vẽ biểu đồ động!

### 3. **Quick Links (Liên kết nhanh)**

- 📊 View MySQL Data - Xem dữ liệu MySQL
- 📈 MySQL Analytics - Phân tích MySQL chi tiết
- 📚 View MongoDB Data - Xem dữ liệu MongoDB
- 📊 MongoDB Analytics - Phân tích MongoDB chi tiết

## 🔧 API Endpoints

Dashboard sử dụng 2 API endpoints:

### 1. GET `/api/report/dashboard-stats`

Trả về statistics tổng quan:
```json
{
  "totalCustomers": 4372,
  "totalOrders": 541911,
  "totalRevenue": 9747747.93,
  "activeProducts": 4070
}
```

### 2. GET `/api/report/chart-data?type={type}`

**Tham số:**
- `type=sales` - Dữ liệu cho biểu đồ quốc gia
- `type=products` - Dữ liệu cho biểu đồ sản phẩm

**Response mẫu:**
```json
{
  "labels": ["United Kingdom", "Germany", "France", ...],
  "values": [8187806.36, 1289301.32, 198011.65, ...]
}
```

## 🎨 Thiết Kế

Dashboard có thiết kế hiện đại với:

- ✨ **Gradient Cards** - 4 màu gradient khác nhau cho mỗi thẻ thống kê
- 🎭 **Hover Effects** - Hiệu ứng nâng lên khi hover
- 📱 **Responsive Design** - Tương thích với mọi kích thước màn hình
- 📊 **Interactive Charts** - Biểu đồ có thể tương tác
- 🎨 **Bootstrap 5** - Framework CSS hiện đại
- 📈 **Chart.js 4** - Thư viện vẽ biểu đồ mạnh mẽ

## 🔍 Cách Hoạt Động

### 1. Server-Side Rendering (Thymeleaf)

```java
@GetMapping
public String dashboard(Model model) {
    Map<String, Object> stats = mySqlService.getOverallStats();
    model.addAttribute("totalRecords", stats.get("totalRecords"));
    // ... thêm các attributes khác
    return "dashboard";
}
```

Dữ liệu được truyền vào template qua `Model`.

### 2. Client-Side Data Loading (JavaScript)

```javascript
async function loadDashboard() {
    const response = await fetch('/api/report/dashboard-stats');
    const stats = await response.json();
    // Update UI với dữ liệu nhận được
}
```

Charts được load động qua AJAX calls.

### 3. Data Flow

```
MySQL Database 
    ↓
OnlineRetailJpaRepository (JPA queries)
    ↓
OnlineRetailMySqlService (Business logic)
    ↓
DashboardController / ReportApi (Controllers)
    ↓
dashboard.html (Thymeleaf + Chart.js)
    ↓
Browser (User sees dashboard)
```

## 📝 Dữ Liệu Hiển Thị

Dashboard lấy dữ liệu từ bảng `online_retail` trong MySQL:

- **Total Records**: `SELECT COUNT(*) FROM online_retail`
- **Unique Customers**: Đếm số lượng distinct customers
- **Countries**: Lấy danh sách countries và tính tổng revenue
- **Top Products**: Nhóm theo stock_code, tính tổng quantity
- **Top Customers**: Nhóm theo customer_id, tính tổng spent

## 🎯 Ví Dụ Sử Dụng

### Xem Thống Kê Nhanh
1. Truy cập http://localhost:8080/dashboard
2. Xem 4 thẻ thống kê ở đầu trang
3. Kiểm tra Total Revenue được tính tự động

### Phân Tích Biểu Đồ
1. Cuộn xuống phần charts
2. Xem Top 10 Countries - biết được quốc gia nào bán nhiều nhất
3. Xem Top 10 Products - biết được sản phẩm nào bán chạy nhất

### Truy Cập Dữ Liệu Chi Tiết
1. Click vào một trong các Quick Links
2. Ví dụ: Click "View MySQL Data" để xem toàn bộ dữ liệu
3. Hoặc click "MySQL Analytics" để xem phân tích chi tiết hơn

## 🐛 Troubleshooting

### Dashboard không hiển thị dữ liệu?

1. **Kiểm tra MySQL connection:**
   ```powershell
   # Xem logs khi start application
   # Tìm dòng: "Fetching MySQL retail data"
   ```

2. **Kiểm tra database có dữ liệu:**
   ```sql
   SELECT COUNT(*) FROM online_retail;
   -- Phải trả về > 0 records
   ```

3. **Kiểm tra browser console:**
   ```
   F12 → Console tab
   Xem có lỗi JavaScript không
   ```

### Charts không hiển thị?

1. **Kiểm tra Chart.js đã load:**
   ```
   F12 → Network tab
   Tìm: chart.js (phải status 200)
   ```

2. **Kiểm tra API response:**
   ```
   Truy cập trực tiếp:
   http://localhost:8080/api/report/dashboard-stats
   http://localhost:8080/api/report/chart-data?type=sales
   ```

### Số liệu không chính xác?

- Revenue calculation là ước tính từ country stats
- Có thể tối ưu bằng cách tạo aggregate query riêng
- Check method `getOverallStats()` trong service

## 🚀 Tối Ưu Hóa

### Để dashboard load nhanh hơn:

1. **Thêm caching:**
   ```java
   @Cacheable("dashboard-stats")
   public Map<String, Object> getOverallStats() { ... }
   ```

2. **Tạo materialized view trong MySQL:**
   ```sql
   CREATE TABLE dashboard_stats AS
   SELECT COUNT(*) as total_records,
          COUNT(DISTINCT customer_id) as unique_customers,
          SUM(quantity * unit_price) as total_revenue
   FROM online_retail;
   ```

3. **Sử dụng pagination cho charts:**
   ```java
   // Chỉ lấy top 10 thay vì toàn bộ
   repository.findTopProducts(PageRequest.of(0, 10))
   ```

## 📚 Tài Liệu Liên Quan

- [MySQL Integration Guide](MYSQL_INTEGRATION_GUIDE.md)
- [MongoDB Integration Guide](MONGODB_INTEGRATION_GUIDE.md)
- [API Reference](docs/api_reference.md)

---

**Tạo bởi:** GitHub Copilot  
**Ngày:** October 27, 2025  
**Project:** DSS Decision Support System
