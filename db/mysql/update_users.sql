-- Script cập nhật thông tin tài khoản
-- ⚠️ WARNING: Sử dụng PLAIN TEXT PASSWORD - KHÔNG AN TOÀN!
-- Chỉ dùng cho Development/Testing
-- Password: "password123" (không mã hóa)

USE dss_db_mysql;

-- Xóa dữ liệu cũ (nếu có)
DELETE FROM users;

-- Thêm lại các tài khoản với PLAIN PASSWORD
-- Cấu trúc: (id, username, password, full_name, role, email, enabled)
INSERT INTO users (id, username, password, full_name, role, email, enabled) VALUES
(1, 'admin', 'password123', 'Administrator', 'ADMIN', 'admin@dss.com', 1),
(2, 'inventory', 'password123', 'Inventory Manager', 'INVENTORY', 'inventory@dss.com', 1),
(3, 'marketing', 'password123', 'Marketing Manager', 'MARKETING', 'marketing@dss.com', 1),
(4, 'sales', 'password123', 'Sales Manager', 'SALES', 'sales@dss.com', 1),
(5, 'staff', 'password123', 'Data Staff', 'STAFF', 'staff@dss.com', 1);

-- Reset AUTO_INCREMENT
ALTER TABLE users AUTO_INCREMENT = 6;

-- Hiển thị kết quả
SELECT id, username, password, full_name, email, role, enabled FROM users ORDER BY id;
