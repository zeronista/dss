-- Cách 3: Sử dụng LOAD DATA INFILE - NHANH NHẤT
-- Chạy lệnh này trong MySQL Workbench hoặc MySQL CLI

-- Bước 1: Tạo bảng (nếu chưa có)
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

-- Bước 2: Load dữ liệu từ CSV
-- Chú ý: Đường dẫn phải dùng / thay vì \
LOAD DATA INFILE 'f:/FPT/S8/DSS301/G5_GP1/dss/online_retail.csv'
INTO TABLE online_retail
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS  -- Bỏ qua header
(invoice_no, stock_code, description, quantity, invoice_date, unit_price, @customer_id, country)
SET customer_id = NULLIF(@customer_id, '');

-- Kiểm tra dữ liệu đã insert
SELECT COUNT(*) as total_rows FROM online_retail;
SELECT * FROM online_retail LIMIT 10;

-- Nếu gặp lỗi về quyền, chạy lệnh này:
-- SHOW VARIABLES LIKE 'secure_file_priv';
-- Sau đó copy file CSV vào thư mục được phép hoặc set:
-- SET GLOBAL local_infile = 1;
