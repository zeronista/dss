-- =====================================================
-- QUICK REFERENCE: Chạy nhanh trong 2 phút
-- =====================================================

-- BƯỚC 1: Tạo database và table (Copy & Paste vào MySQL)
CREATE DATABASE IF NOT EXISTS dss_db_mysql;
USE dss_db_mysql;

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

-- BƯỚC 2: Kiểm tra
DESCRIBE online_retail;

-- BƯỚC 3: Load data (chọn 1 cách)
-- Cách A: Python (Khuyến nghị)
--   cd db/etl
--   pip install pandas sqlalchemy pymysql
--   python load_csv_pandas.py

-- Cách B: SQL LOAD DATA
-- LOAD DATA LOCAL INFILE 'f:/FPT/S8/DSS301/G5_GP1/dss/online_retail.csv'
-- INTO TABLE online_retail
-- FIELDS TERMINATED BY ',' ENCLOSED BY '"'
-- LINES TERMINATED BY '\n' IGNORE 1 ROWS
-- (invoice_no, stock_code, description, quantity, invoice_date, unit_price, @customer_id, country)
-- SET customer_id = NULLIF(@customer_id, '');

-- BƯỚC 4: Kiểm tra data
SELECT COUNT(*) FROM online_retail;
SELECT * FROM online_retail LIMIT 5;

-- XONG! 🎉
