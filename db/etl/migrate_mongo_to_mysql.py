"""
MongoDB to MySQL Data Migration Script
Chuyển đổi dữ liệu từ MongoDB DSS sang MySQL dss_db_mysql
"""

import pymongo
import mysql.connector
from datetime import datetime
import re

# MongoDB Connection
MONGO_URI = "mongodb+srv://vuthanhlam848:vuthanhlam848@cluster0.s9cdtme.mongodb.net/DSS?retryWrites=true&w=majority"
MONGO_DB = "DSS"

# MySQL Connection
MYSQL_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '1234',
    'database': 'dss_db_mysql'
}

def connect_mongodb():
    """Kết nối MongoDB"""
    client = pymongo.MongoClient(MONGO_URI)
    db = client[MONGO_DB]
    return db

def connect_mysql():
    """Kết nối MySQL"""
    conn = mysql.connector.connect(**MYSQL_CONFIG)
    return conn

def extract_customer_id(invoice_no):
    """Trích xuất customer ID từ InvoiceNo"""
    # Format: CustomerID từ InvoiceNo
    return str(invoice_no) if invoice_no else None

def parse_invoice_date(date_str):
    """Chuyển đổi invoice date sang datetime"""
    try:
        # Format: "2010-12-01 08:26:00"
        return datetime.strptime(date_str, "%Y-%m-%d %H:%M:%S")
    except:
        return datetime.now()

def migrate_customers(mongo_db, mysql_conn):
    """Migrate unique customers từ MongoDB"""
    print("🔄 Migrating Customers...")
    
    cursor = mysql_conn.cursor()
    
    # Lấy unique customers từ MongoDB
    pipeline = [
        {
            "$group": {
                "_id": "$CustomerID",
                "country": {"$first": "$Country"},
                "firstPurchase": {"$min": "$InvoiceDate"}
            }
        }
    ]
    
    customers = list(mongo_db.DSS.aggregate(pipeline))
    
    # Insert vào MySQL
    insert_query = """
    INSERT IGNORE INTO customers (customer_id, name, email, address, registered_date)
    VALUES (%s, %s, %s, %s, %s)
    """
    
    count = 0
    for customer in customers:
        customer_id = str(customer['_id'])
        country = customer.get('country', 'Unknown')
        first_purchase = parse_invoice_date(customer.get('firstPurchase', ''))
        
        # Generate email
        email = f"customer{customer_id}@dss.com"
        name = f"Customer {customer_id}"
        
        cursor.execute(insert_query, (
            customer_id,
            name,
            email,
            country,
            first_purchase
        ))
        count += 1
    
    mysql_conn.commit()
    print(f"✅ Inserted {count} customers")

def migrate_products(mongo_db, mysql_conn):
    """Migrate unique products từ MongoDB"""
    print("🔄 Migrating Products...")
    
    cursor = mysql_conn.cursor()
    
    # Lấy unique products từ MongoDB
    pipeline = [
        {
            "$group": {
                "_id": "$StockCode",
                "description": {"$first": "$Description"},
                "price": {"$avg": "$UnitPrice"}
            }
        }
    ]
    
    products = list(mongo_db.DSS.aggregate(pipeline))
    
    # Insert vào MySQL
    insert_query = """
    INSERT IGNORE INTO products (product_id, name, description, price, stock_quantity)
    VALUES (%s, %s, %s, %s, %s)
    """
    
    count = 0
    for product in products:
        stock_code = str(product['_id'])
        description = product.get('description', 'No Description')
        price = round(product.get('price', 0), 2)
        
        cursor.execute(insert_query, (
            stock_code,
            description[:255],  # Limit name length
            description,
            price,
            100  # Default stock quantity
        ))
        count += 1
    
    mysql_conn.commit()
    print(f"✅ Inserted {count} products")

def migrate_orders(mongo_db, mysql_conn):
    """Migrate orders và order items từ MongoDB"""
    print("🔄 Migrating Orders...")
    
    cursor = mysql_conn.cursor()
    
    # Lấy unique invoices từ MongoDB
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
        {"$limit": 10000}  # Limit để test
    ]
    
    orders = list(mongo_db.DSS.aggregate(pipeline))
    
    # Insert orders
    order_query = """
    INSERT IGNORE INTO orders (order_id, customer_id, order_date, status, total_amount)
    VALUES (%s, %s, %s, %s, %s)
    """
    
    # Insert order items
    item_query = """
    INSERT IGNORE INTO order_items (order_id, product_id, product_name, quantity, price, subtotal)
    VALUES (%s, %s, %s, %s, %s, %s)
    """
    
    order_count = 0
    item_count = 0
    
    for order in orders:
        invoice_no = str(order['_id'])
        customer_id = str(order.get('customerId', ''))
        order_date = parse_invoice_date(order.get('invoiceDate', ''))
        items = order.get('items', [])
        
        # Calculate total
        total_amount = sum(
            item.get('quantity', 0) * item.get('unitPrice', 0) 
            for item in items
        )
        
        # Insert order
        try:
            cursor.execute(order_query, (
                invoice_no,
                customer_id,
                order_date,
                'COMPLETED',
                round(total_amount, 2)
            ))
            order_count += 1
            
            # Insert order items
            for item in items:
                stock_code = str(item.get('stockCode', ''))
                description = item.get('description', 'No Description')
                quantity = item.get('quantity', 0)
                unit_price = item.get('unitPrice', 0)
                subtotal = quantity * unit_price
                
                cursor.execute(item_query, (
                    invoice_no,
                    stock_code,
                    description[:255],
                    quantity,
                    round(unit_price, 2),
                    round(subtotal, 2)
                ))
                item_count += 1
                
        except Exception as e:
            print(f"⚠️  Error inserting order {invoice_no}: {e}")
            continue
    
    mysql_conn.commit()
    print(f"✅ Inserted {order_count} orders with {item_count} items")

def main():
    """Main migration function"""
    print("=" * 60)
    print("MongoDB → MySQL Data Migration")
    print("=" * 60)
    
    try:
        # Connect to databases
        print("\n📡 Connecting to MongoDB...")
        mongo_db = connect_mongodb()
        print("✅ MongoDB connected")
        
        print("\n📡 Connecting to MySQL...")
        mysql_conn = connect_mysql()
        print("✅ MySQL connected")
        
        # Run migrations
        print("\n" + "=" * 60)
        migrate_customers(mongo_db, mysql_conn)
        print()
        migrate_products(mongo_db, mysql_conn)
        print()
        migrate_orders(mongo_db, mysql_conn)
        
        # Close connections
        mysql_conn.close()
        print("\n" + "=" * 60)
        print("✅ Migration completed successfully!")
        print("=" * 60)
        
    except Exception as e:
        print(f"\n❌ Migration failed: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()
