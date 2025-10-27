-- =====================================================
-- SCRIPT NÂNG CAO: Tạo bảng với partitioning và optimization
-- Phù hợp cho dữ liệu lớn (541K+ rows)
-- =====================================================

USE dss_db_mysql;

-- Xóa bảng cũ (cẩn thận!)
DROP TABLE IF EXISTS online_retail;

-- Tạo bảng với partitioning theo năm
CREATE TABLE online_retail (
    id BIGINT AUTO_INCREMENT,
    invoice_no VARCHAR(20) NOT NULL,
    stock_code VARCHAR(50) NOT NULL,
    description TEXT,
    quantity INT NOT NULL,
    invoice_date DATETIME NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    customer_id INT NULL,
    country VARCHAR(100) NOT NULL,
    
    -- Computed columns
    total_amount DECIMAL(12, 2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    year INT GENERATED ALWAYS AS (YEAR(invoice_date)) STORED,
    month INT GENERATED ALWAYS AS (MONTH(invoice_date)) STORED,
    
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Primary key
    PRIMARY KEY (id, invoice_date),
    
    -- Indexes
    KEY idx_invoice_no (invoice_no),
    KEY idx_stock_code (stock_code),
    KEY idx_customer_id (customer_id),
    KEY idx_country (country),
    KEY idx_year_month (year, month),
    KEY idx_invoice_date (invoice_date),
    
    -- Composite indexes cho queries phức tạp
    KEY idx_customer_date (customer_id, invoice_date),
    KEY idx_product_date (stock_code, invoice_date)
    
) ENGINE=InnoDB 
DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_unicode_ci
ROW_FORMAT=DYNAMIC
PARTITION BY RANGE (YEAR(invoice_date)) (
    PARTITION p2010 VALUES LESS THAN (2011),
    PARTITION p2011 VALUES LESS THAN (2012),
    PARTITION p2012 VALUES LESS THAN (2013),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- Tạo view để dễ query
CREATE OR REPLACE VIEW v_online_retail_summary AS
SELECT 
    invoice_no,
    invoice_date,
    customer_id,
    country,
    COUNT(*) as item_count,
    SUM(quantity) as total_quantity,
    SUM(total_amount) as total_revenue
FROM online_retail
GROUP BY invoice_no, invoice_date, customer_id, country;

-- Tạo view top sản phẩm
CREATE OR REPLACE VIEW v_top_products AS
SELECT 
    stock_code,
    description,
    COUNT(*) as times_ordered,
    SUM(quantity) as total_sold,
    AVG(unit_price) as avg_price,
    SUM(total_amount) as total_revenue
FROM online_retail
GROUP BY stock_code, description
ORDER BY total_revenue DESC;

-- Tạo view top khách hàng
CREATE OR REPLACE VIEW v_top_customers AS
SELECT 
    customer_id,
    country,
    COUNT(DISTINCT invoice_no) as order_count,
    SUM(total_amount) as total_spent,
    AVG(total_amount) as avg_order_value,
    MIN(invoice_date) as first_order,
    MAX(invoice_date) as last_order
FROM online_retail
WHERE customer_id IS NOT NULL
GROUP BY customer_id, country
ORDER BY total_spent DESC;

-- Hiển thị kết quả
SHOW CREATE TABLE online_retail;
DESCRIBE online_retail;
SELECT * FROM information_schema.partitions 
WHERE table_name = 'online_retail' AND table_schema = 'dss_db_mysql';
