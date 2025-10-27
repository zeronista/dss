"""
Script kiểm tra kết nối MySQL và tạo table
"""
import mysql.connector
from mysql.connector import Error

def test_mysql_connection():
    """
    Kiểm tra kết nối đến MySQL
    """
    # Thông tin kết nối - SỬA LẠI CHO ĐÚNG
    config = {
        'host': 'localhost',
        'user': 'root',
        'password': 'your_password',  # ⚠️ THAY ĐỔI PASSWORD
        'port': 3306
    }
    
    try:
        print("Đang kết nối đến MySQL...")
        connection = mysql.connector.connect(**config)
        
        if connection.is_connected():
            db_info = connection.get_server_info()
            print(f"✅ Kết nối thành công!")
            print(f"MySQL Server version: {db_info}")
            
            cursor = connection.cursor()
            cursor.execute("SELECT DATABASE();")
            record = cursor.fetchone()
            print(f"Connected to database: {record}")
            
            # Hiển thị tất cả databases
            cursor.execute("SHOW DATABASES;")
            databases = cursor.fetchall()
            print("\n📁 Danh sách databases:")
            for db in databases:
                print(f"  - {db[0]}")
            
            return True
            
    except Error as e:
        print(f"❌ Lỗi kết nối: {e}")
        return False
        
    finally:
        if 'connection' in locals() and connection.is_connected():
            cursor.close()
            connection.close()
            print("\n✅ Đã đóng kết nối MySQL")

def create_database_and_table():
    """
    Tạo database và table cho online retail
    """
    config = {
        'host': 'localhost',
        'user': 'root',
        'password': 'your_password',  # ⚠️ THAY ĐỔI PASSWORD
        'port': 3306
    }
    
    try:
        connection = mysql.connector.connect(**config)
        cursor = connection.cursor()
        
        # Tạo database
        print("\n📦 Tạo database...")
        cursor.execute("CREATE DATABASE IF NOT EXISTS dss_db;")
        cursor.execute("USE dss_db;")
        print("✅ Database 'dss_db' đã sẵn sàng")
        
        # Tạo table
        print("\n📋 Tạo table 'online_retail'...")
        create_table_query = """
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
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """
        
        cursor.execute(create_table_query)
        print("✅ Table 'online_retail' đã được tạo")
        
        # Kiểm tra structure
        cursor.execute("DESCRIBE online_retail;")
        columns = cursor.fetchall()
        print("\n📊 Cấu trúc table:")
        print(f"{'Field':<20} {'Type':<20} {'Key':<10}")
        print("-" * 50)
        for col in columns:
            print(f"{col[0]:<20} {col[1]:<20} {col[3]:<10}")
        
        connection.commit()
        return True
        
    except Error as e:
        print(f"❌ Lỗi: {e}")
        return False
        
    finally:
        if 'connection' in locals() and connection.is_connected():
            cursor.close()
            connection.close()

if __name__ == "__main__":
    print("=" * 60)
    print("KIỂM TRA KẾT NỐI MYSQL")
    print("=" * 60)
    
    # Bước 1: Test connection
    if test_mysql_connection():
        # Bước 2: Tạo database và table
        create_database_and_table()
        
        print("\n" + "=" * 60)
        print("🎉 HOÀN TẤT! Bạn có thể chạy script load CSV ngay")
        print("=" * 60)
    else:
        print("\n⚠️  Vui lòng kiểm tra lại thông tin kết nối MySQL")
