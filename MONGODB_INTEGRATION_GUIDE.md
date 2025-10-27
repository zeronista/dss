# Hướng dẫn lấy dữ liệu từ MongoDB và hiển thị trên Web

## 📋 Tổng quan

Đã tạo đầy đủ components để lấy dữ liệu từ MongoDB Atlas (collection "DSS") và hiển thị trên web.

---

## 🎯 Files đã tạo

### 1. **Domain/Entity Layer**
```
src/main/java/com/g5/dss/domain/mongo/
└── OnlineRetailDocument.java      // MongoDB Document Entity
```

### 2. **Repository Layer**
```
src/main/java/com/g5/dss/repository/mongo/
└── OnlineRetailRepository.java    // MongoDB Repository với các query methods
```

### 3. **DTO Layer**
```
src/main/java/com/g5/dss/dto/
├── OnlineRetailDTO.java          // Data Transfer Object
└── PagedResponse.java            // Pagination response wrapper
```

### 4. **Service Layer**
```
src/main/java/com/g5/dss/service/
└── OnlineRetailService.java      // Business logic
```

### 5. **Controller Layer**
```
src/main/java/com/g5/dss/api/
└── OnlineRetailApiController.java     // REST API endpoints

src/main/java/com/g5/dss/controller/
└── OnlineRetailController.java        // Web page controller
```

### 6. **View Layer**
```
src/main/resources/templates/
├── retail_data.html              // Trang danh sách dữ liệu
└── invoice_detail.html           // Trang chi tiết hóa đơn
```

---

## 🚀 Cách sử dụng

### A. REST API Endpoints

#### 1. Lấy tất cả dữ liệu (có phân trang)
```
GET http://localhost:8080/api/retail?page=0&size=20&sortBy=invoiceDate
```

#### 2. Tìm kiếm
```
GET http://localhost:8080/api/retail/search?keyword=HEART&page=0&size=20
```

#### 3. Lấy theo Invoice Number
```
GET http://localhost:8080/api/retail/invoice/536365
```

#### 4. Lấy theo Customer ID
```
GET http://localhost:8080/api/retail/customer/17850?page=0&size=20
```

#### 5. Lấy theo Country
```
GET http://localhost:8080/api/retail/country/United%20Kingdom?page=0&size=20
```

#### 6. Lấy theo Stock Code
```
GET http://localhost:8080/api/retail/product/85123A
```

#### 7. Thống kê
```
GET http://localhost:8080/api/retail/stats
```

#### 8. Danh sách quốc gia
```
GET http://localhost:8080/api/retail/countries
```

---

### B. Web Pages

#### 1. Trang danh sách dữ liệu
```
URL: http://localhost:8080/retail
```

**Features:**
- ✅ Hiển thị data với phân trang
- ✅ Tìm kiếm theo keyword
- ✅ Thống kê tổng quan
- ✅ Responsive table
- ✅ Click vào Invoice để xem chi tiết

**Parameters:**
- `page` - Số trang (default: 0)
- `size` - Số items/trang (default: 50)
- `search` - Từ khóa tìm kiếm

**Ví dụ:**
```
http://localhost:8080/retail?page=0&size=50
http://localhost:8080/retail?search=HEART&page=0
```

#### 2. Trang chi tiết hóa đơn
```
URL: http://localhost:8080/retail/invoice?invoiceNo=536365
```

**Features:**
- ✅ Hiển thị tất cả items trong invoice
- ✅ Tính tổng tiền
- ✅ Thông tin khách hàng
- ✅ Định dạng đẹp

---

## 🔧 Cấu hình

### 1. MongoDB Connection (đã có sẵn)
```yaml
# application.yml
spring:
  data:
    mongodb:
      uri: mongodb+srv://vuthanhlam848:vuthanhlam848@cluster0.s9cdtme.mongodb.net/DSS?retryWrites=true&w=majority
      database: DSS
```

### 2. Build và Run
```bash
# Clean và build
mvn clean install

# Run application
mvn spring-boot:run
```

Hoặc trong VS Code: Run DssApplication.java

---

## 📊 Data Structure từ MongoDB

```json
{
  "_id": "68fdf360a4970830abc5864a",
  "InvoiceNo": "536365",
  "StockCode": "85123A",
  "Description": "WHITE HANGING HEART T-LIGHT HOLDER",
  "Quantity": 6,
  "InvoiceDate": "2010-12-01 08:26:00",
  "UnitPrice": 2.55,
  "CustomerID": 17850,
  "Country": "United Kingdom"
}
```

---

## 💡 Ví dụ sử dụng trong JavaScript

### Fetch data với JavaScript
```javascript
// Lấy tất cả data
fetch('http://localhost:8080/api/retail?page=0&size=20')
  .then(response => response.json())
  .then(data => {
    console.log('Total records:', data.totalElements);
    console.log('Data:', data.content);
  });

// Tìm kiếm
fetch('http://localhost:8080/api/retail/search?keyword=HEART&page=0&size=20')
  .then(response => response.json())
  .then(data => console.log(data));

// Lấy theo invoice
fetch('http://localhost:8080/api/retail/invoice/536365')
  .then(response => response.json())
  .then(items => console.log('Invoice items:', items));
```

### Sử dụng với jQuery
```javascript
$.ajax({
  url: 'http://localhost:8080/api/retail',
  method: 'GET',
  data: { page: 0, size: 20 },
  success: function(response) {
    console.log(response);
  }
});
```

### Sử dụng với Axios
```javascript
axios.get('http://localhost:8080/api/retail', {
  params: { page: 0, size: 20 }
})
.then(response => {
  console.log(response.data);
});
```

---

## 🎨 Tùy chỉnh

### Thay đổi số items per page
Mở `OnlineRetailController.java` và sửa dòng:
```java
@RequestParam(defaultValue = "50") int size
```

### Thay đổi sort order
Mở `OnlineRetailService.java` và sửa trong method `getAllData`:
```java
Sort.by(sortBy).ascending()  // Thay vì .descending()
```

### Thêm filters
Thêm method mới trong `OnlineRetailRepository.java`:
```java
List<OnlineRetailDocument> findByQuantityGreaterThan(Integer minQuantity);
```

---

## 🔍 Testing

### Test REST API với cURL
```bash
# Get all data
curl http://localhost:8080/api/retail?page=0&size=5

# Search
curl "http://localhost:8080/api/retail/search?keyword=HEART&page=0&size=5"

# Get by invoice
curl http://localhost:8080/api/retail/invoice/536365

# Get stats
curl http://localhost:8080/api/retail/stats
```

### Test với Postman
1. Import collection với các endpoints trên
2. Test từng endpoint
3. Kiểm tra response format

---

## ✅ Checklist

- [x] MongoDB Entity (OnlineRetailDocument)
- [x] MongoDB Repository với custom queries
- [x] Service layer với business logic
- [x] REST API Controller với 8 endpoints
- [x] Web Controller cho 2 pages
- [x] HTML templates (retail_data.html, invoice_detail.html)
- [x] Pagination support
- [x] Search functionality
- [x] Responsive design
- [x] Error handling

---

## 🎯 Next Steps

1. **Run Application**: `mvn spring-boot:run`
2. **Truy cập**: http://localhost:8080/retail
3. **Test API**: http://localhost:8080/api/retail
4. **Customize**: Sửa templates theo design của bạn

---

## 📞 API Response Format

### Paginated Response
```json
{
  "content": [
    {
      "id": "68fdf360a4970830abc5864a",
      "invoiceNo": "536365",
      "stockCode": "85123A",
      "description": "WHITE HANGING HEART T-LIGHT HOLDER",
      "quantity": 6,
      "invoiceDate": "2010-12-01 08:26:00",
      "unitPrice": 2.55,
      "customerId": 17850,
      "country": "United Kingdom",
      "totalAmount": 15.30
    }
  ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 541909,
  "totalPages": 27096,
  "first": true,
  "last": false
}
```

### Stats Response
```json
{
  "totalRecords": 541909,
  "countries": ["United Kingdom", "France", "Germany", ...]
}
```

Chúc bạn thành công! 🎉
