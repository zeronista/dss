# MySQL Scripts

Thư mục chứa các SQL scripts cho MySQL database.

---

## 📁 Files mới - Online Retail Table

### 🚀 Quick Start
**File**: `QUICK_START.sql`
- Script siêu nhanh (2 phút)
- Copy & paste trực tiếp vào MySQL
- Tạo table và sẵn sàng load data

### 📋 Scripts tạo table

| File | Mức độ | Mô tả |
|------|--------|-------|
| `simple_create_table.sql` | ⭐ Cơ bản | Đơn giản, dễ hiểu |
| `create_table_online_retail.sql` | ⭐⭐ Khuyến nghị | Có indexes, comments |
| `advanced_create_table.sql` | ⭐⭐⭐ Nâng cao | Partitioning, Views |

### 📖 Hướng dẫn
**File**: `TABLE_CREATION_GUIDE.md`
- Hướng dẫn chi tiết từng bước
- So sánh các scripts
- Troubleshooting

### 🔧 SQL cho Load Data
**File**: `load_csv_direct.sql`
- Sử dụng LOAD DATA INFILE
- Nhanh nhất để import CSV

---

## 📊 Cấu trúc Table: online_retail

```sql
CREATE TABLE online_retail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_no VARCHAR(20),
    stock_code VARCHAR(50),
    description TEXT,
    quantity INT,
    invoice_date DATETIME,
    unit_price DECIMAL(10, 2),
    customer_id INT,
    country VARCHAR(100),
    created_at TIMESTAMP
);
```

**Indexes**: invoice_no, customer_id, invoice_date

---

## 🎯 Cách sử dụng

### Chạy SQL Script

**Trong MySQL Workbench:**
```
File → Open SQL Script → Chọn file → Execute
```

**Trong MySQL CLI:**
```bash
mysql -u root -p1234 < QUICK_START.sql
```

**Trong PowerShell:**
```powershell
Get-Content .\QUICK_START.sql | mysql -u root -p1234
```

---

## 📝 Files khác

- `schema_mysql.sql` - Schema gốc của project
- `update_users.sql` - Update users table
- `data_from_mongodb.sql` - Data migrated từ MongoDB

---

## ✅ Checklist

- [ ] Tạo database: `CREATE DATABASE dss_db_mysql;`
- [ ] Chạy một trong các script tạo table
- [ ] Kiểm tra: `DESCRIBE online_retail;`
- [ ] Load data từ CSV (xem `../etl/`)
- [ ] Verify: `SELECT COUNT(*) FROM online_retail;`

---

## 📞 Hỗ trợ

Xem hướng dẫn chi tiết: `TABLE_CREATION_GUIDE.md`
