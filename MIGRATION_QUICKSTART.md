# 🚀 Quick Start: MongoDB to MySQL Migration

## Bước 1: Install dependencies
```powershell
cd f:\FPT\S8\DSS301\G5_GP1\dss\db\etl
pip install pymongo mysql-connector-python
```

## Bước 2: Chạy migration script
```powershell
# Option A: Tạo SQL file (khuyến nghị - dễ review)
python generate_sql_from_mongodb.py

# Output: db/mysql/data_from_mongodb.sql
```

## Bước 3: Import vào MySQL
```powershell
cd f:\FPT\S8\DSS301\G5_GP1\dss
Get-Content db\mysql\data_from_mongodb.sql | mysql -u root -p1234
```

## Hoặc: Direct Migration (nhanh hơn)
```powershell
cd f:\FPT\S8\DSS301\G5_GP1\dss\db\etl
python migrate_mongo_to_mysql.py
```

## ✅ Kiểm tra kết quả
```sql
mysql -u root -p1234 -D dss_db_mysql -e "
SELECT 'Customers' as tbl, COUNT(*) as cnt FROM customers
UNION ALL
SELECT 'Products', COUNT(*) FROM products
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders;
"
```

---

**Dữ liệu từ MongoDB DSS (542K documents)**:
- ✅ Customers: Unique CustomerID
- ✅ Products: Unique StockCode + Description
- ✅ Orders: InvoiceNo với items
- ✅ Order Items: Chi tiết sản phẩm trong order

**Lưu ý**: Scripts có giới hạn để test. Xóa `{"$limit": ...}` để migrate toàn bộ dữ liệu.
