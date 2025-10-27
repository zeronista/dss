# Hướng dẫn Load CSV vào MySQL

## 📋 Tổng quan

File này hướng dẫn 3 cách để load dữ liệu từ `online_retail.csv` vào MySQL database.

## 🎯 Các cách thực hiện

### **Cách 1: Python với mysql-connector** ⭐
- **Ưu điểm**: Kiểm soát tốt, xử lý lỗi chi tiết
- **Nhược điểm**: Chậm hơn Pandas
- **File**: `load_csv_to_mysql.py`

### **Cách 2: Python với Pandas** ⭐⭐⭐ (KHUYẾN NGHỊ)
- **Ưu điểm**: Nhanh, code ngắn gọn, xử lý dữ liệu mạnh
- **Nhược điểm**: Cần nhiều thư viện
- **File**: `load_csv_pandas.py`

### **Cách 3: MySQL LOAD DATA INFILE** ⭐⭐⭐⭐
- **Ưu điểm**: NHANH NHẤT, trực tiếp
- **Nhược điểm**: Cần quyền MySQL, khó debug
- **File**: `load_csv_direct.sql`

---

## 🚀 Cài đặt

### 1. Cài đặt thư viện Python

```bash
cd f:\FPT\S8\DSS301\G5_GP1\dss\db\etl
pip install -r requirements.txt
```

Hoặc cài từng thư viện:

```bash
pip install mysql-connector-python pandas sqlalchemy pymysql
```

### 2. Tạo Database và Table

Chạy lệnh SQL sau trong MySQL:

```sql
CREATE DATABASE IF NOT EXISTS dss_db;
USE dss_db;

CREATE TABLE IF NOT EXISTS online_retail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_no VARCHAR(20),
    stock_code VARCHAR(50),
    description TEXT,
    quantity INT,
    invoice_date DATETIME,
    unit_price DECIMAL(10, 2),
    customer_id INT,
    country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_invoice_no (invoice_no),
    INDEX idx_customer_id (customer_id),
    INDEX idx_invoice_date (invoice_date)
);
```

---

## 📝 Hướng dẫn sử dụng

### **Cách 1: Dùng Python mysql-connector**

1. Mở file `load_csv_to_mysql.py`
2. Sửa thông tin kết nối:
   ```python
   db_config = {
       'host': 'localhost',
       'user': 'root',
       'password': 'your_password',
       'database': 'dss_db'
   }
   ```
3. Chạy script:
   ```bash
   python load_csv_to_mysql.py
   ```

### **Cách 2: Dùng Pandas (KHUYẾN NGHỊ)** 

1. Mở file `load_csv_pandas.py`
2. Sửa thông tin kết nối:
   ```python
   DB_CONFIG = {
       'host': 'localhost',
       'user': 'root',
       'password': 'your_password',
       'database': 'dss_db',
       'port': 3306
   }
   ```
3. Chạy script:
   ```bash
   python load_csv_pandas.py
   ```

### **Cách 3: Dùng MySQL LOAD DATA INFILE**

1. Kiểm tra quyền:
   ```sql
   SHOW VARIABLES LIKE 'secure_file_priv';
   ```

2. Nếu cần, enable local infile:
   ```sql
   SET GLOBAL local_infile = 1;
   ```

3. Chạy file SQL:
   ```bash
   mysql -u root -p dss_db < load_csv_direct.sql
   ```

   Hoặc trong MySQL Workbench, mở và chạy `load_csv_direct.sql`

---

## 🔍 Kiểm tra kết quả

Sau khi load, kiểm tra dữ liệu:

```sql
-- Đếm tổng số dòng
SELECT COUNT(*) FROM online_retail;

-- Xem 10 dòng đầu
SELECT * FROM online_retail LIMIT 10;

-- Kiểm tra dữ liệu theo quốc gia
SELECT country, COUNT(*) as count 
FROM online_retail 
GROUP BY country 
ORDER BY count DESC 
LIMIT 10;

-- Kiểm tra dữ liệu theo ngày
SELECT DATE(invoice_date) as date, COUNT(*) as count 
FROM online_retail 
GROUP BY date 
ORDER BY date 
LIMIT 10;
```

---

## 🐛 Xử lý lỗi thường gặp

### Lỗi: "Access denied for user"
- Kiểm tra username/password MySQL
- Đảm bảo user có quyền INSERT vào database

### Lỗi: "Table doesn't exist"
- Chạy lại script tạo table trong bước 2

### Lỗi: "The used command is not allowed with this MySQL version"
- Chạy: `SET GLOBAL local_infile = 1;`
- Kết nối lại MySQL

### Lỗi: "Module not found"
- Cài đặt lại thư viện: `pip install -r requirements.txt`

---

## 💡 Tips

1. **Backup trước khi load**: Luôn backup database trước
2. **Test với ít dữ liệu**: Thử với vài dòng đầu tiên
3. **Kiểm tra encoding**: Đảm bảo CSV là UTF-8
4. **Monitor performance**: Dùng batch insert cho hiệu suất tốt

---

## 📊 So sánh hiệu suất

| Phương pháp | Tốc độ | Độ khó | RAM | Khuyến nghị |
|-------------|--------|--------|-----|-------------|
| mysql-connector | Chậm | Dễ | Thấp | Dữ liệu nhỏ |
| Pandas | Nhanh | Trung bình | Cao | **Khuyến nghị** |
| LOAD DATA INFILE | Rất nhanh | Khó | Thấp | Dữ liệu lớn |

---

## 📞 Hỗ trợ

Nếu gặp vấn đề, kiểm tra:
1. MySQL đã chạy chưa
2. Thông tin kết nối đúng chưa
3. File CSV tồn tại và có quyền đọc
4. Thư viện Python đã cài đủ chưa
