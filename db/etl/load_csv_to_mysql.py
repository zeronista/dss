import csv
import mysql.connector
from datetime import datetime

# Cấu hình kết nối MySQL
db_config = {
    'host': 'localhost',
    'user': 'root',  # Thay bằng username của bạn
    'password': '1234',  # Thay bằng password của bạn
    'database': 'dss_db_mysql'  # Thay bằng tên database của bạn
}

def load_csv_to_mysql(csv_file_path):
    """
    Đọc file CSV và insert từng dòng vào MySQL
    """
    try:
        # Kết nối đến MySQL
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor()
        
        # Mở file CSV
        with open(csv_file_path, 'r', encoding='utf-8') as csv_file:
            csv_reader = csv.DictReader(csv_file)
            
            # SQL INSERT statement
            insert_query = """
                INSERT INTO online_retail 
                (invoice_no, stock_code, description, quantity, invoice_date, 
                 unit_price, customer_id, country)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
            """
            
            # Đếm số dòng đã insert
            count = 0
            batch_size = 1000
            batch_data = []
            
            # Đọc từng dòng trong CSV
            for row in csv_reader:
                try:
                    # Lấy dữ liệu từ mỗi cột
                    invoice_no = row['InvoiceNo']
                    stock_code = row['StockCode']
                    description = row['Description']
                    quantity = int(row['Quantity']) if row['Quantity'] else None
                    
                    # Parse date
                    invoice_date = datetime.strptime(row['InvoiceDate'], '%Y-%m-%d %H:%M:%S')
                    
                    unit_price = float(row['UnitPrice']) if row['UnitPrice'] else None
                    customer_id = int(float(row['CustomerID'])) if row['CustomerID'] else None
                    country = row['Country']
                    
                    # Thêm vào batch
                    batch_data.append((
                        invoice_no, stock_code, description, quantity,
                        invoice_date, unit_price, customer_id, country
                    ))
                    
                    # Insert theo batch để tăng hiệu suất
                    if len(batch_data) >= batch_size:
                        cursor.executemany(insert_query, batch_data)
                        connection.commit()
                        count += len(batch_data)
                        print(f"Đã insert {count} dòng...")
                        batch_data = []
                        
                except Exception as e:
                    print(f"Lỗi khi xử lý dòng: {row}")
                    print(f"Chi tiết lỗi: {str(e)}")
                    continue
            
            # Insert batch cuối cùng
            if batch_data:
                cursor.executemany(insert_query, batch_data)
                connection.commit()
                count += len(batch_data)
            
            print(f"\n✅ Hoàn thành! Đã insert tổng cộng {count} dòng vào MySQL")
            
    except mysql.connector.Error as e:
        print(f"❌ Lỗi MySQL: {e}")
    except Exception as e:
        print(f"❌ Lỗi: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("Đã đóng kết nối MySQL")

if __name__ == "__main__":
    # Đường dẫn đến file CSV
    csv_file = r"f:\FPT\S8\DSS301\G5_GP1\dss\online_retail.csv"
    
    print("Bắt đầu load dữ liệu từ CSV vào MySQL...")
    load_csv_to_mysql(csv_file)
