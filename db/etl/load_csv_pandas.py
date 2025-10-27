import pandas as pd
from sqlalchemy import create_engine

# Cấu hình kết nối MySQL
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',  # Thay bằng username của bạn
    'password': 'your_password',  # Thay bằng password của bạn
    'database': 'dss_db',  # Thay bằng tên database của bạn
    'port': 3306
}

def load_csv_with_pandas(csv_file_path):
    """
    Sử dụng Pandas để load CSV vào MySQL - NHANH NHẤT
    """
    try:
        # Tạo connection string
        connection_string = (
            f"mysql+pymysql://{DB_CONFIG['user']}:{DB_CONFIG['password']}"
            f"@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}"
        )
        
        # Tạo engine
        engine = create_engine(connection_string)
        
        print("Đang đọc file CSV...")
        # Đọc CSV với Pandas
        df = pd.read_csv(csv_file_path)
        
        print(f"Đã đọc {len(df)} dòng từ CSV")
        print("\nThông tin dữ liệu:")
        print(df.info())
        print("\n5 dòng đầu tiên:")
        print(df.head())
        
        # Đổi tên cột cho khớp với database (nếu cần)
        df.columns = [
            'invoice_no', 'stock_code', 'description', 'quantity',
            'invoice_date', 'unit_price', 'customer_id', 'country'
        ]
        
        # Xử lý dữ liệu
        df['invoice_date'] = pd.to_datetime(df['invoice_date'])
        df['customer_id'] = df['customer_id'].fillna(0).astype(int)
        
        # Insert vào MySQL
        print("\nĐang insert vào MySQL...")
        df.to_sql(
            name='online_retail',  # Tên bảng
            con=engine,
            if_exists='append',  # 'append': thêm vào, 'replace': thay thế
            index=False,
            chunksize=1000,  # Insert theo batch 1000 dòng
            method='multi'  # Tăng tốc độ insert
        )
        
        print(f"✅ Hoàn thành! Đã insert {len(df)} dòng vào MySQL")
        
    except Exception as e:
        print(f"❌ Lỗi: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    csv_file = r"f:\FPT\S8\DSS301\G5_GP1\dss\online_retail.csv"
    load_csv_with_pandas(csv_file)
