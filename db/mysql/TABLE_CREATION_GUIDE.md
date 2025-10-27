# Hướng dẫn tạo Table cho Online Retail CSV

## 📋 Các Script SQL đã tạo

### 1. **simple_create_table.sql** ⭐ (KHUYẾN NGHỊ)
- Đơn giản, dễ hiểu
- Phù hợp cho mọi trường hợp
- Chứa cả hướng dẫn load data

**Cách dùng:**
```sql
-- Mở MySQL Workbench hoặc MySQL CLI
source f:/FPT/S8/DSS301/G5_GP1/dss/db/mysql/simple_create_table.sql;
```

Hoặc:
```powershell
# Trong PowerShell
Get-Content db\mysql\simple_create_table.sql | mysql -u root -p1234
```

---

### 2. **create_table_online_retail.sql** ⭐⭐
- Chi tiết, có comment đầy đủ
- Có indexes để tăng tốc query
- Kèm các query mẫu hữu ích

**Cách dùng:**
```sql
source f:/FPT/S8/DSS301/G5_GP1/dss/db/mysql/create_table_online_retail.sql;
```

---

### 3. **advanced_create_table.sql** ⭐⭐⭐
- Nâng cao với partitioning
- Tự động tính toán total_amount
- Tạo sẵn các view cho analysis
- Phù hợp cho dữ liệu lớn (541K+ rows)

**Cách dùng:**
```sql
source f:/FPT/S8/DSS301/G5_GP1/dss/db/mysql/advanced_create_table.sql;
```

---

## 🚀 Quy trình hoàn chỉnh

### Bước 1: Chọn và chạy script tạo table

**Option A: Dùng MySQL Workbench**
1. Mở MySQL Workbench
2. Kết nối đến MySQL Server
3. File → Open SQL Script
4. Chọn một trong các file SQL trên
5. Click Execute (⚡) hoặc Ctrl+Shift+Enter

**Option B: Dùng MySQL CLI**
```powershell
# Đăng nhập MySQL
mysql -u root -p1234

# Chạy script
source f:/FPT/S8/DSS301/G5_GP1/dss/db/mysql/simple_create_table.sql;

# Hoặc
USE dss_db_mysql;
\. f:/FPT/S8/DSS301/G5_GP1/dss/db/mysql/simple_create_table.sql
```

**Option C: Dùng PowerShell**
```powershell
# Vào thư mục project
cd f:\FPT\S8\DSS301\G5_GP1\dss

# Chạy script
Get-Content db\mysql\simple_create_table.sql | mysql -u root -p1234
```

---

### Bước 2: Kiểm tra table đã được tạo

```sql
-- Kiểm tra database
SHOW DATABASES;

-- Sử dụng database
USE dss_db_mysql;

-- Xem danh sách tables
SHOW TABLES;

-- Xem cấu trúc table
DESCRIBE online_retail;

-- Hoặc chi tiết hơn
SHOW CREATE TABLE online_retail;

-- Kiểm tra indexes
SHOW INDEX FROM online_retail;
```

---

### Bước 3: Load dữ liệu từ CSV

**Cách 1: Dùng Python Script (KHUYẾN NGHỊ)**
```powershell
# Cài đặt thư viện (chỉ cần 1 lần)
pip install mysql-connector-python pandas sqlalchemy pymysql

# Chạy script load data
cd db\etl
python load_csv_pandas.py
```

**Cách 2: Dùng LOAD DATA INFILE**
```sql
-- Enable local infile
SET GLOBAL local_infile = 1;

-- Load data
LOAD DATA LOCAL INFILE 'f:/FPT/S8/DSS301/G5_GP1/dss/online_retail.csv'
INTO TABLE online_retail
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(invoice_no, stock_code, description, quantity, invoice_date, unit_price, @customer_id, country)
SET customer_id = NULLIF(@customer_id, '');
```

---

### Bước 4: Kiểm tra dữ liệu

```sql
-- Đếm tổng số records
SELECT COUNT(*) as total FROM online_retail;

-- Xem 10 dòng đầu
SELECT * FROM online_retail LIMIT 10;

-- Thống kê cơ bản
SELECT 
    COUNT(*) as total_records,
    COUNT(DISTINCT invoice_no) as total_invoices,
    COUNT(DISTINCT customer_id) as total_customers,
    COUNT(DISTINCT stock_code) as total_products,
    MIN(invoice_date) as first_date,
    MAX(invoice_date) as last_date
FROM online_retail;

-- Top 10 quốc gia
SELECT country, COUNT(*) as orders
FROM online_retail
GROUP BY country
ORDER BY orders DESC
LIMIT 10;
```

---

## 📊 Cấu trúc Table

| Column | Type | Description |
|--------|------|-------------|
| id | INT AUTO_INCREMENT | Primary Key |
| invoice_no | VARCHAR(20) | Mã hóa đơn |
| stock_code | VARCHAR(50) | Mã sản phẩm |
| description | TEXT | Mô tả sản phẩm |
| quantity | INT | Số lượng |
| invoice_date | DATETIME | Ngày giờ hóa đơn |
| unit_price | DECIMAL(10,2) | Đơn giá |
| customer_id | INT (NULL) | Mã khách hàng |
| country | VARCHAR(100) | Quốc gia |
| created_at | TIMESTAMP | Thời gian tạo |

---

## 🎯 So sánh các Script

| Feature | Simple | Standard | Advanced |
|---------|--------|----------|----------|
| Độ phức tạp | Thấp | Trung bình | Cao |
| Indexes | Không | Có | Có + Composite |
| Partitioning | Không | Không | Có (theo năm) |
| Computed Columns | Không | Không | Có (total_amount) |
| Views | Không | Không | Có (3 views) |
| Performance | Tốt | Rất tốt | Xuất sắc |
| Khuyến nghị | Học tập | **Production** | Big Data |

---

## 💡 Tips

1. **Chọn script phù hợp**:
   - Mới bắt đầu → `simple_create_table.sql`
   - Sử dụng thực tế → `create_table_online_retail.sql`
   - Dữ liệu lớn → `advanced_create_table.sql`

2. **Backup trước khi chạy**:
   ```sql
   -- Backup database
   mysqldump -u root -p1234 dss_db_mysql > backup.sql
   ```

3. **Optimize sau khi insert**:
   ```sql
   OPTIMIZE TABLE online_retail;
   ANALYZE TABLE online_retail;
   ```

4. **Monitor performance**:
   ```sql
   -- Xem kích thước table
   SELECT 
       table_name,
       ROUND(((data_length + index_length) / 1024 / 1024), 2) AS "Size (MB)"
   FROM information_schema.TABLES 
   WHERE table_schema = "dss_db_mysql" 
   AND table_name = "online_retail";
   ```

---

## ❓ Troubleshooting

### Lỗi: "Table already exists"
```sql
-- Xóa table cũ (cẩn thận!)
DROP TABLE IF EXISTS online_retail;
-- Sau đó chạy lại script
```

### Lỗi: "Access denied"
```sql
-- Cấp quyền cho user
GRANT ALL PRIVILEGES ON dss_db_mysql.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### Lỗi: "Unknown database"
```sql
-- Tạo database trước
CREATE DATABASE IF NOT EXISTS dss_db_mysql;
USE dss_db_mysql;
```

---

## 📞 Hỗ trợ

Nếu gặp vấn đề:
1. Kiểm tra MySQL đang chạy
2. Kiểm tra thông tin đăng nhập (username/password)
3. Xem log lỗi: `SHOW WARNINGS;`
4. Xem các query đang chạy: `SHOW PROCESSLIST;`
