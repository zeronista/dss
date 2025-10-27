"""
Load CSV vào MySQL sử dụng Environment Variables
An toàn hơn vì không hardcode password trong code
"""
import os
import pandas as pd
from sqlalchemy import create_engine
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

def get_db_config():
    """Lấy config từ .env file"""
    return {
        'host': os.getenv('MYSQL_HOST', 'localhost'),
        'port': os.getenv('MYSQL_PORT', '3306'),
        'user': os.getenv('MYSQL_USER', 'root'),
        'password': os.getenv('MYSQL_PASSWORD'),
        'database': os.getenv('MYSQL_DATABASE', 'dss_db')
    }

def load_csv_secure():
    """
    Load CSV vào MySQL sử dụng config từ .env
    """
    try:
        # Lấy config
        config = get_db_config()
        csv_file = os.getenv('CSV_FILE_PATH', 'online_retail.csv')
        batch_size = int(os.getenv('BATCH_SIZE', '1000'))
        
        # Kiểm tra password
        if not config['password']:
            print("❌ Chưa thiết lập MYSQL_PASSWORD trong file .env")
            print("💡 Tạo file .env từ .env.example và điền thông tin")
            return False
        
        # Tạo connection string
        connection_string = (
            f"mysql+pymysql://{config['user']}:{config['password']}"
            f"@{config['host']}:{config['port']}/{config['database']}"
        )
        
        print(f"📁 Đang đọc file: {csv_file}")
        print(f"🔗 Kết nối đến: {config['host']}:{config['port']}/{config['database']}")
        
        # Tạo engine
        engine = create_engine(connection_string)
        
        # Đọc CSV
        df = pd.read_csv(csv_file)
        print(f"✅ Đã đọc {len(df):,} dòng từ CSV")
        
        # Xử lý dữ liệu
        df.columns = [
            'invoice_no', 'stock_code', 'description', 'quantity',
            'invoice_date', 'unit_price', 'customer_id', 'country'
        ]
        
        df['invoice_date'] = pd.to_datetime(df['invoice_date'])
        df['customer_id'] = df['customer_id'].fillna(0).astype(int)
        
        # Insert vào MySQL
        print(f"📤 Đang insert vào MySQL (batch size: {batch_size})...")
        df.to_sql(
            name='online_retail',
            con=engine,
            if_exists='append',
            index=False,
            chunksize=batch_size,
            method='multi'
        )
        
        print(f"✅ Hoàn thành! Đã insert {len(df):,} dòng")
        return True
        
    except FileNotFoundError:
        print(f"❌ Không tìm thấy file: {csv_file}")
        return False
    except Exception as e:
        print(f"❌ Lỗi: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    print("=" * 60)
    print("LOAD CSV VÀO MYSQL (Secure Mode)")
    print("=" * 60)
    
    # Kiểm tra .env file
    if not os.path.exists('.env'):
        print("\n⚠️  Chưa có file .env")
        print("📝 Tạo file .env từ .env.example:")
        print("   1. Copy .env.example thành .env")
        print("   2. Sửa thông tin trong .env")
        print("   3. Chạy lại script này")
    else:
        load_csv_secure()
