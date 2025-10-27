# 🚀 Quick Start - Lấy dữ liệu từ MongoDB

## ✅ Đã tạo xong!

Tất cả components để lấy dữ liệu từ MongoDB và hiển thị trên web đã sẵn sàng.

---

## 🎯 Cách sử dụng

### 1. Run Application
```bash
mvn spring-boot:run
```

### 2. Truy cập Web
```
URL: http://localhost:8080/retail
```

### 3. Hoặc dùng API
```
GET http://localhost:8080/api/retail?page=0&size=20
```

---

## 📁 Files đã tạo

✅ `OnlineRetailDocument.java` - MongoDB Entity  
✅ `OnlineRetailRepository.java` - Repository  
✅ `OnlineRetailService.java` - Service  
✅ `OnlineRetailApiController.java` - REST API  
✅ `OnlineRetailController.java` - Web Controller  
✅ `retail_data.html` - Trang danh sách  
✅ `invoice_detail.html` - Trang chi tiết  

---

## 🌐 Endpoints

| URL | Mô tả |
|-----|-------|
| `/retail` | Trang web hiển thị data |
| `/retail/invoice?invoiceNo=536365` | Chi tiết hóa đơn |
| `/api/retail` | REST API - Lấy tất cả |
| `/api/retail/search?keyword=HEART` | Tìm kiếm |
| `/api/retail/invoice/536365` | API - Chi tiết invoice |
| `/api/retail/customer/17850` | API - Theo customer |
| `/api/retail/stats` | API - Thống kê |

---

## 📖 Hướng dẫn chi tiết

Xem file: `MONGODB_INTEGRATION_GUIDE.md`

---

## 🎉 Hoàn tất!

Chạy app và truy cập: **http://localhost:8080/retail**
