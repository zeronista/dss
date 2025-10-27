"""
Script đơn giản để đọc và hiển thị từng dòng CSV
Giúp bạn hiểu cách lấy data riêng biệt từ CSV
"""
import csv

def read_csv_line_by_line(csv_file_path):
    """
    Đọc file CSV và in ra từng dòng dữ liệu
    """
    print("=" * 80)
    print("ĐỌC FILE CSV TỪNG DÒNG")
    print("=" * 80)
    
    with open(csv_file_path, 'r', encoding='utf-8') as file:
        # Tạo CSV reader
        csv_reader = csv.DictReader(file)
        
        # Đọc 5 dòng đầu tiên
        for i, row in enumerate(csv_reader, 1):
            if i > 5:  # Chỉ hiển thị 5 dòng đầu
                break
                
            print(f"\n--- DÒNG {i} ---")
            print(f"Invoice No:    {row['InvoiceNo']}")
            print(f"Stock Code:    {row['StockCode']}")
            print(f"Description:   {row['Description']}")
            print(f"Quantity:      {row['Quantity']}")
            print(f"Invoice Date:  {row['InvoiceDate']}")
            print(f"Unit Price:    {row['UnitPrice']}")
            print(f"Customer ID:   {row['CustomerID']}")
            print(f"Country:       {row['Country']}")
    
    print("\n" + "=" * 80)
    print("CÁCH LẤY DỮ LIỆU:")
    print("=" * 80)
    print("""
    1. Sử dụng csv.DictReader: row['TenCot']
       Ví dụ: invoice_no = row['InvoiceNo']
    
    2. Sử dụng csv.reader: row[index]
       Ví dụ: invoice_no = row[0]
    
    3. Sử dụng Pandas:
       df = pd.read_csv('file.csv')
       df['InvoiceNo'] - lấy toàn bộ cột
       df.iloc[0] - lấy dòng đầu tiên
       df.loc[0, 'InvoiceNo'] - lấy giá trị cụ thể
    """)

def read_csv_with_list(csv_file_path):
    """
    Đọc CSV vào list để truy xuất dễ dàng
    """
    print("\n" + "=" * 80)
    print("ĐỌC CSV VÀO LIST")
    print("n" * 80)
    
    data_list = []
    
    with open(csv_file_path, 'r', encoding='utf-8') as file:
        csv_reader = csv.DictReader(file)
        
        # Lấy 10 dòng đầu
        for i, row in enumerate(csv_reader):
            if i >= 10:
                break
            data_list.append(row)
    
    print(f"Đã load {len(data_list)} dòng vào list")
    print(f"\nTruy xuất dòng thứ 1:")
    print(data_list[0])
    
    print(f"\nTruy xuất Invoice No của dòng thứ 3:")
    print(data_list[2]['InvoiceNo'])
    
    print(f"\nLấy tất cả Invoice No:")
    invoice_numbers = [row['InvoiceNo'] for row in data_list]
    print(invoice_numbers)
    
    return data_list

def demo_pandas_way(csv_file_path):
    """
    Cách sử dụng Pandas để lấy data
    """
    try:
        import pandas as pd
        
        print("\n" + "=" * 80)
        print("CÁCH DÙNG PANDAS")
        print("=" * 80)
        
        # Đọc CSV
        df = pd.read_csv(csv_file_path, nrows=10)  # Chỉ đọc 10 dòng
        
        print("\nThông tin DataFrame:")
        print(df.info())
        
        print("\n5 dòng đầu:")
        print(df.head())
        
        print("\nLấy cột InvoiceNo:")
        print(df['InvoiceNo'])
        
        print("\nLấy dòng đầu tiên:")
        print(df.iloc[0])
        
        print("\nLấy giá trị cụ thể (dòng 2, cột Description):")
        print(df.loc[1, 'Description'])
        
        print("\nLọc dữ liệu (Quantity > 5):")
        print(df[df['Quantity'] > 5])
        
    except ImportError:
        print("\n⚠️  Chưa cài Pandas. Chạy: pip install pandas")

if __name__ == "__main__":
    # Đường dẫn file CSV
    csv_file = r"f:\FPT\S8\DSS301\G5_GP1\dss\online_retail.csv"
    
    # Thử các cách đọc
    read_csv_line_by_line(csv_file)
    data = read_csv_with_list(csv_file)
    demo_pandas_way(csv_file)
