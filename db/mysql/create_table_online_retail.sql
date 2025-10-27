-- =====================================================
-- Script: Tạo bảng online_retail từ CSV
-- Database: dss_db_mysql
-- Author: G5_GP1
-- Date: 2025-10-27
-- =====================================================

-- Sử dụng database
USE dss_db_mysql;

-- Xóa bảng cũ nếu tồn tại (cẩn thận!)
-- DROP TABLE IF EXISTS online_retail;

-- Tạo bảng online_retail
CREATE TABLE IF NOT EXISTS online_retail (
    -- Primary Key
    id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Thông tin hóa đơn
    invoice_no VARCHAR(20) NOT NULL COMMENT 'Mã hóa đơn',
    invoice_date DATETIME NOT NULL COMMENT 'Ngày giờ tạo hóa đơn',
    
    -- Thông tin sản phẩm
    stock_code VARCHAR(50) NOT NULL COMMENT 'Mã sản phẩm',
    description TEXT COMMENT 'Mô tả sản phẩm',
    
    -- Thông tin giao dịch
    quantity INT NOT NULL COMMENT 'Số lượng mua',
    unit_price DECIMAL(10, 2) NOT NULL COMMENT 'Đơn giá',
    
    -- Thông tin khách hàng
    customer_id INT NULL COMMENT 'Mã khách hàng (có thể null)',
    country VARCHAR(100) NOT NULL COMMENT 'Quốc gia',
    
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo record',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    
    -- Indexes để tăng tốc độ query
    INDEX idx_invoice_no (invoice_no),
    INDEX idx_stock_code (stock_code),
    INDEX idx_customer_id (customer_id),
    INDEX idx_invoice_date (invoice_date),
    INDEX idx_country (country),
    INDEX idx_invoice_customer (invoice_no, customer_id)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Bảng lưu trữ dữ liệu giao dịch bán lẻ từ online_retail.csv';

-- Kiểm tra bảng đã được tạo
DESCRIBE online_retail;

-- Hiển thị thông tin bảng
SHOW CREATE TABLE online_retail;

-- =====================================================
-- Các query hữu ích sau khi insert data
-- =====================================================

-- Đếm tổng số records
-- SELECT COUNT(*) as total_records FROM online_retail;

-- Xem 10 dòng đầu
-- SELECT * FROM online_retail LIMIT 10;

-- Thống kê theo quốc gia
-- SELECT country, COUNT(*) as orders, SUM(quantity * unit_price) as revenue
-- FROM online_retail 
-- GROUP BY country 
-- ORDER BY revenue DESC;

-- Thống kê theo sản phẩm
-- SELECT stock_code, description, 
--        COUNT(*) as times_sold, 
--        SUM(quantity) as total_quantity,
--        AVG(unit_price) as avg_price
-- FROM online_retail 
-- GROUP BY stock_code, description 
-- ORDER BY times_sold DESC 
-- LIMIT 20;

-- Thống kê theo khách hàng
-- SELECT customer_id, 
--        COUNT(DISTINCT invoice_no) as total_orders,
--        SUM(quantity * unit_price) as total_spent,
--        MAX(invoice_date) as last_order_date
-- FROM online_retail 
-- WHERE customer_id IS NOT NULL
-- GROUP BY customer_id 
-- ORDER BY total_spent DESC 
-- LIMIT 20;

-- Thống kê theo tháng
-- SELECT 
--     DATE_FORMAT(invoice_date, '%Y-%m') as month,
--     COUNT(DISTINCT invoice_no) as orders,
--     COUNT(*) as items_sold,
--     SUM(quantity * unit_price) as revenue
-- FROM online_retail 
-- GROUP BY month 
-- ORDER BY month;
