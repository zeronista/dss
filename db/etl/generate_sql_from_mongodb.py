"""
Generate SQL Insert Statements từ MongoDB Data
Tạo file SQL để import vào MySQL
"""

import pymongo
from datetime import datetime
import json

# MongoDB Connection
MONGO_URI = "mongodb+srv://vuthanhlam848:vuthanhlam848@cluster0.s9cdtme.mongodb.net/DSS?retryWrites=true&w=majority"
MONGO_DB = "DSS"

def connect_mongodb():
    """Kết nối MongoDB"""
    client = pymongo.MongoClient(MONGO_URI)
    db = client[MONGO_DB]
    return db

def escape_sql_string(s):
    """Escape string cho SQL"""
    if s is None:
        return 'NULL'
    s = str(s).replace("'", "''").replace("\\", "\\\\")
    return f"'{s}'"

def parse_date(date_str):
    """Parse date string"""
    try:
        dt = datetime.strptime(date_str, "%Y-%m-%d %H:%M:%S")
        return f"'{dt.strftime('%Y-%m-%d %H:%M:%S')}'"
    except:
        return f"'{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}'"

def generate_customers_sql(mongo_db):
    """Generate SQL cho customers"""
    print("🔄 Generating Customers SQL...")
    
    pipeline = [
        {
            "$group": {
                "_id": "$CustomerID",
                "country": {"$first": "$Country"},
                "firstPurchase": {"$min": "$InvoiceDate"}
            }
        },
        {"$limit": 1000}  # Limit
    ]
    
    customers = list(mongo_db.DSS.aggregate(pipeline))
    
    sql_lines = []
    sql_lines.append("-- Customers Data")
    sql_lines.append("DELETE FROM customers WHERE customer_id IS NOT NULL;")
    sql_lines.append("")
    
    for customer in customers:
        customer_id = escape_sql_string(customer['_id'])
        country = escape_sql_string(customer.get('country', 'Unknown'))
        first_purchase = parse_date(customer.get('firstPurchase', ''))
        email = escape_sql_string(f"customer{customer['_id']}@dss.com")
        name = escape_sql_string(f"Customer {customer['_id']}")
        
        sql = f"INSERT INTO customers (customer_id, name, email, address, registered_date) VALUES ({customer_id}, {name}, {email}, {country}, {first_purchase});"
        sql_lines.append(sql)
    
    print(f"✅ Generated {len(customers)} customer records")
    return "\n".join(sql_lines)

def generate_products_sql(mongo_db):
    """Generate SQL cho products"""
    print("🔄 Generating Products SQL...")
    
    pipeline = [
        {
            "$group": {
                "_id": "$StockCode",
                "description": {"$first": "$Description"},
                "price": {"$avg": "$UnitPrice"}
            }
        },
        {"$limit": 1000}
    ]
    
    products = list(mongo_db.DSS.aggregate(pipeline))
    
    sql_lines = []
    sql_lines.append("\n-- Products Data")
    sql_lines.append("DELETE FROM products WHERE product_id IS NOT NULL;")
    sql_lines.append("")
    
    for product in products:
        stock_code = escape_sql_string(product['_id'])
        description = escape_sql_string(product.get('description', 'No Description')[:255])
        desc_full = escape_sql_string(product.get('description', 'No Description'))
        price = round(product.get('price', 0), 2)
        
        sql = f"INSERT INTO products (product_id, name, description, price, stock_quantity) VALUES ({stock_code}, {description}, {desc_full}, {price}, 100);"
        sql_lines.append(sql)
    
    print(f"✅ Generated {len(products)} product records")
    return "\n".join(sql_lines)

def generate_orders_sql(mongo_db):
    """Generate SQL cho orders"""
    print("🔄 Generating Orders SQL...")
    
    pipeline = [
        {
            "$group": {
                "_id": "$InvoiceNo",
                "customerId": {"$first": "$CustomerID"},
                "invoiceDate": {"$first": "$InvoiceDate"},
                "items": {
                    "$push": {
                        "stockCode": "$StockCode",
                        "description": "$Description",
                        "quantity": "$Quantity",
                        "unitPrice": "$UnitPrice"
                    }
                }
            }
        },
        {"$limit": 500}  # Limit orders
    ]
    
    orders = list(mongo_db.DSS.aggregate(pipeline))
    
    sql_lines = []
    sql_lines.append("\n-- Orders Data")
    sql_lines.append("DELETE FROM order_items WHERE order_id IS NOT NULL;")
    sql_lines.append("DELETE FROM orders WHERE order_id IS NOT NULL;")
    sql_lines.append("")
    
    for order in orders:
        invoice_no = escape_sql_string(order['_id'])
        customer_id = escape_sql_string(order.get('customerId', ''))
        order_date = parse_date(order.get('invoiceDate', ''))
        items = order.get('items', [])
        
        total_amount = sum(
            item.get('quantity', 0) * item.get('unitPrice', 0) 
            for item in items
        )
        
        # Order SQL
        sql = f"INSERT INTO orders (order_id, customer_id, order_date, status, total_amount) VALUES ({invoice_no}, {customer_id}, {order_date}, 'COMPLETED', {round(total_amount, 2)});"
        sql_lines.append(sql)
        
        # Order Items SQL
        for item in items:
            stock_code = escape_sql_string(item.get('stockCode', ''))
            description = escape_sql_string(item.get('description', 'No Description')[:255])
            quantity = item.get('quantity', 0)
            unit_price = round(item.get('unitPrice', 0), 2)
            subtotal = round(quantity * unit_price, 2)
            
            # Note: order_items table might need order's ID (not order_id string)
            # Adjust based on your schema
            sql_item = f"-- Item for order {invoice_no}: {stock_code}, qty={quantity}, price={unit_price}"
            sql_lines.append(sql_item)
    
    print(f"✅ Generated {len(orders)} order records")
    return "\n".join(sql_lines)

def main():
    """Main function"""
    print("=" * 60)
    print("MongoDB → MySQL SQL Generator")
    print("=" * 60)
    
    try:
        print("\n📡 Connecting to MongoDB...")
        mongo_db = connect_mongodb()
        print("✅ Connected\n")
        
        # Generate SQL
        customers_sql = generate_customers_sql(mongo_db)
        products_sql = generate_products_sql(mongo_db)
        orders_sql = generate_orders_sql(mongo_db)
        
        # Write to file
        output_file = "f:/FPT/S8/DSS301/G5_GP1/dss/db/mysql/data_from_mongodb.sql"
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write("-- Auto-generated SQL from MongoDB Data\n")
            f.write(f"-- Generated at: {datetime.now()}\n")
            f.write("-- Database: dss_db_mysql\n\n")
            f.write("USE dss_db_mysql;\n\n")
            f.write(customers_sql)
            f.write("\n\n")
            f.write(products_sql)
            f.write("\n\n")
            f.write(orders_sql)
        
        print(f"\n✅ SQL file saved to: {output_file}")
        print("=" * 60)
        print("📌 Next steps:")
        print("   1. Review the SQL file")
        print("   2. Run: mysql -u root -p1234 < db/mysql/data_from_mongodb.sql")
        print("=" * 60)
        
    except Exception as e:
        print(f"\n❌ Error: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()
