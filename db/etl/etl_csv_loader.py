"""
ETL Script for Loading CSV Data into Database
Supports both MongoDB and MySQL
"""

import pandas as pd
import pymongo
import mysql.connector
from pathlib import Path
import argparse
import json


class ETLLoader:
    def __init__(self, db_type='mongo', connection_string=None):
        self.db_type = db_type
        self.connection_string = connection_string
        
        if db_type == 'mongo':
            self.client = pymongo.MongoClient(connection_string or 'mongodb://localhost:27017/')
            self.db = self.client['dss_db']
        elif db_type == 'mysql':
            # Parse MySQL connection string
            # Format: mysql://user:password@host:port/database
            self.connection = mysql.connector.connect(
                host='localhost',
                user='root',
                password='password',
                database='dss_db'
            )
            self.cursor = self.connection.cursor()
    
    def load_customers(self, csv_path):
        """Load customers from CSV"""
        df = pd.read_csv(csv_path)
        
        if self.db_type == 'mongo':
            records = df.to_dict('records')
            self.db.customers.insert_many(records)
        elif self.db_type == 'mysql':
            for _, row in df.iterrows():
                query = """
                    INSERT INTO customers (customer_id, name, email, phone, address, segment)
                    VALUES (%s, %s, %s, %s, %s, %s)
                """
                self.cursor.execute(query, tuple(row))
            self.connection.commit()
        
        print(f"Loaded {len(df)} customers")
    
    def load_products(self, csv_path):
        """Load products from CSV"""
        df = pd.read_csv(csv_path)
        
        if self.db_type == 'mongo':
            records = df.to_dict('records')
            self.db.products.insert_many(records)
        elif self.db_type == 'mysql':
            for _, row in df.iterrows():
                query = """
                    INSERT INTO products (product_id, name, category, price, stock_quantity, description)
                    VALUES (%s, %s, %s, %s, %s, %s)
                """
                self.cursor.execute(query, tuple(row))
            self.connection.commit()
        
        print(f"Loaded {len(df)} products")
    
    def load_orders(self, csv_path):
        """Load orders from CSV"""
        df = pd.read_csv(csv_path)
        
        if self.db_type == 'mongo':
            records = df.to_dict('records')
            # Convert items from JSON string to list
            for record in records:
                if 'items' in record and isinstance(record['items'], str):
                    record['items'] = json.loads(record['items'])
            self.db.orders.insert_many(records)
        elif self.db_type == 'mysql':
            for _, row in df.iterrows():
                query = """
                    INSERT INTO orders (order_id, customer_id, order_date, status, 
                                      total_amount, payment_method, is_returned, return_risk_score)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                """
                self.cursor.execute(query, tuple(row[:8]))
            self.connection.commit()
        
        print(f"Loaded {len(df)} orders")
    
    def close(self):
        """Close database connections"""
        if self.db_type == 'mongo':
            self.client.close()
        elif self.db_type == 'mysql':
            self.cursor.close()
            self.connection.close()


def main():
    parser = argparse.ArgumentParser(description='ETL Data Loader')
    parser.add_argument('--db-type', choices=['mongo', 'mysql'], default='mongo', 
                        help='Database type')
    parser.add_argument('--customers', type=str, help='Path to customers CSV')
    parser.add_argument('--products', type=str, help='Path to products CSV')
    parser.add_argument('--orders', type=str, help='Path to orders CSV')
    
    args = parser.parse_args()
    
    loader = ETLLoader(db_type=args.db_type)
    
    try:
        if args.customers:
            loader.load_customers(args.customers)
        
        if args.products:
            loader.load_products(args.products)
        
        if args.orders:
            loader.load_orders(args.orders)
        
        print("ETL process completed successfully!")
    
    except Exception as e:
        print(f"Error during ETL: {e}")
    
    finally:
        loader.close()


if __name__ == '__main__':
    main()

