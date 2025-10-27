-- Script đơn giản: Tạo bảng và load data từ CSV

-- Bước 1: Tạo database (nếu chưa có)
CREATE DATABASE IF NOT EXISTS dss_db_mysql;
USE dss_db_mysql;

-- Bước 2: Tạo bảng
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bước 3: Kiểm tra bảng
DESCRIBE online_retail;

-- Bước 4: Load data từ CSV (tùy chọn)
-- Cách 1: Dùng LOAD DATA INFILE (nhanh nhất)
/*
LOAD DATA LOCAL INFILE 'f:/FPT/S8/DSS301/G5_GP1/dss/online_retail.csv'
INTO TABLE online_retail
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(invoice_no, stock_code, description, quantity, invoice_date, unit_price, @customer_id, country)
SET customer_id = NULLIF(@customer_id, '');
*/

-- Cách 2: Dùng Python script (khuyến nghị)
-- Chạy: python db/etl/load_csv_pandas.py

-- Bước 5: Kiểm tra data sau khi insert
-- SELECT COUNT(*) FROM online_retail;
-- SELECT * FROM online_retail LIMIT 10;
