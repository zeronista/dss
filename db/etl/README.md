# ETL Scripts - Data Migration & CSV Loading

Thư mục này chứa các scripts để:
1. Chuyển dữ liệu từ MongoDB sang MySQL
2. **Load dữ liệu từ CSV vào MySQL** (MỚI)

---

## 🆕 LOAD CSV VÀO MYSQL

### Quick Start (5 phút)

**Bước 1**: Cài đặt thư viện
```powershell
cd db/etl
pip install -r requirements.txt
```

**Bước 2**: Chọn một trong 3 cách sau:

#### ⭐ Cách 1: Dùng Pandas (KHUYẾN NGHỊ - Nhanh & Dễ)
```powershell
# Mở file load_csv_pandas.py, sửa password
python load_csv_pandas.py
```

#### ⭐ Cách 2: Dùng Environment Variables (AN TOÀN)
```powershell
# Copy .env.example thành .env
# Sửa thông tin trong .env
python load_csv_secure.py
```

#### ⭐ Cách 3: Dùng MySQL LOAD DATA (NHANH NHẤT)
```sql
-- Chạy trong MySQL Workbench
-- File: load_csv_direct.sql
```

### 📚 Chi tiết

- **Quick Start Guide**: `QUICKSTART.md`
- **Hướng dẫn đầy đủ**: `CSV_TO_MYSQL_GUIDE.md`
- **Demo đọc CSV**: `demo_read_csv.py`
- **Test connection**: `test_mysql_connection.py`

### 📁 Files mới

| File | Mô tả |
|------|-------|
| `load_csv_pandas.py` | Load CSV dùng Pandas (khuyến nghị) |
| `load_csv_to_mysql.py` | Load CSV dùng mysql-connector |
| `load_csv_secure.py` | Load CSV dùng .env (secure) |
| `load_csv_direct.sql` | Load CSV dùng LOAD DATA INFILE |
| `demo_read_csv.py` | Demo cách đọc từng dòng CSV |
| `test_mysql_connection.py` | Test kết nối MySQL |
| `CSV_TO_MYSQL_GUIDE.md` | Hướng dẫn chi tiết |
| `QUICKSTART.md` | Hướng dẫn nhanh |
| `.env.example` | Template cho environment variables |

---

## 📋 MongoDB to MySQL Migration

Scripts để chuyển dữ liệu từ MongoDB Atlas sang MySQL local database.

## 📋 Chuẩn bị

### 1. Install Python dependencies

```powershell
cd db/etl
pip install -r requirements.txt
```

### 2. Kiểm tra kết nối

- **MongoDB**: cluster0.s9cdtme.mongodb.net/DSS (542K documents)
- **MySQL**: localhost:3306/dss_db_mysql

## 🚀 Chạy Migration

### Option 1: Tạo SQL File (Khuyến nghị)

Script này sẽ tạo file SQL từ MongoDB data:

```powershell
python generate_sql_from_mongodb.py
```

Output: `db/mysql/data_from_mongodb.sql`

Sau đó import vào MySQL:

```powershell
cd ../..
Get-Content db/mysql/data_from_mongodb.sql | mysql -u root -p1234
```

### Option 2: Direct Migration (Nhanh hơn)

Script này sẽ connect trực tiếp vào MySQL và insert data:

```powershell
python migrate_mongo_to_mysql.py
```

## 📊 Dữ liệu được migrate

### 1. **Customers** (từ CustomerID)
- Unique customers từ collection DSS
- Fields: customer_id, name, email, address (country), registered_date
- Ví dụ: CustomerID 17850 → customer17850@dss.com

### 2. **Products** (từ StockCode)
- Unique products từ collection DSS
- Fields: product_id (StockCode), name, description, price (avg), stock_quantity
- Ví dụ: StockCode "85123A" → "WHITE HANGING HEART T-LIGHT HOLDER"

### 3. **Orders** (từ InvoiceNo)
- Unique invoices từ collection DSS
- Fields: order_id, customer_id, order_date, status, total_amount
- Ví dụ: InvoiceNo 536365 → order với items

### 4. **Order Items** (embedded trong Orders)
- Chi tiết sản phẩm trong mỗi order
- Fields: order_id, product_id, product_name, quantity, price, subtotal

## ⚙️ Cấu hình

### MongoDB
```python
MONGO_URI = "mongodb+srv://vuthanhlam848:...@cluster0.s9cdtme.mongodb.net/DSS"
MONGO_DB = "DSS"
```

### MySQL
```python
MYSQL_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '1234',
    'database': 'dss_db_mysql'
}
```

## 📝 Lưu ý

1. **Limit dữ liệu**: Scripts có limit để test (1000 customers, 1000 products, 500 orders)
2. **Bỏ limit**: Xóa dòng `{"$limit": ...}` trong pipeline để migrate toàn bộ
3. **Duplicate**: Scripts sử dụng `INSERT IGNORE` hoặc `DELETE` trước để tránh duplicate
4. **Performance**: Direct migration nhanh hơn nhưng SQL file dễ review hơn

## 🔍 Kiểm tra sau khi migrate

```sql
-- Đếm số lượng records
SELECT 'Customers' as Table_Name, COUNT(*) as Count FROM customers
UNION ALL
SELECT 'Products', COUNT(*) FROM products
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders
UNION ALL
SELECT 'Order Items', COUNT(*) FROM order_items;

-- Xem sample data
SELECT * FROM customers LIMIT 5;
SELECT * FROM products LIMIT 5;
SELECT * FROM orders LIMIT 5;
```

## 🎯 Mapping MongoDB → MySQL

| MongoDB Field | MySQL Table | MySQL Field |
|--------------|-------------|-------------|
| CustomerID | customers | customer_id |
| Country | customers | address |
| StockCode | products | product_id |
| Description | products | name, description |
| UnitPrice | products | price |
| InvoiceNo | orders | order_id |
| InvoiceDate | orders | order_date |
| Quantity | order_items | quantity |

---

**Created**: 2025-10-27  
**Database**: DSS (MongoDB) → dss_db_mysql (MySQL)
