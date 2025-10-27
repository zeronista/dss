# 🚀 QUICK START: Load CSV vào MySQL

## Các bước thực hiện nhanh (5 phút)

### Bước 1: Cài đặt thư viện
```bash
cd f:\FPT\S8\DSS301\G5_GP1\dss\db\etl
pip install pandas sqlalchemy pymysql mysql-connector-python
```

### Bước 2: Test kết nối MySQL
```bash
# Mở file test_mysql_connection.py
# Sửa password ở dòng 13: 'password': 'your_password'
python test_mysql_connection.py
```

### Bước 3: Load CSV (chọn 1 trong 3 cách)

#### ⭐⭐⭐ Cách Nhanh Nhất - Dùng Pandas:
```bash
# Mở file load_csv_pandas.py
# Sửa password ở dòng 9
python load_csv_pandas.py
```

#### Hoặc dùng mysql-connector:
```bash
# Mở file load_csv_to_mysql.py
# Sửa password ở dòng 10
python load_csv_to_mysql.py
```

#### Hoặc dùng SQL trực tiếp:
```sql
-- Mở MySQL Workbench
-- Chạy file: load_csv_direct.sql
```

### Bước 4: Kiểm tra kết quả
```sql
SELECT COUNT(*) FROM dss_db.online_retail;
SELECT * FROM dss_db.online_retail LIMIT 10;
```

---

## 📚 Đọc thêm
- Chi tiết: `CSV_TO_MYSQL_GUIDE.md`
- Demo đọc CSV: `python demo_read_csv.py`

---

## ❓ Cần giúp?
- Lỗi kết nối: Kiểm tra MySQL đã chạy, username/password đúng
- Lỗi import: `pip install -r requirements.txt`
- Lỗi encoding: Đảm bảo CSV là UTF-8
