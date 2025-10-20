# Database Setup

This directory contains database schemas, initialization scripts, and ETL tools for the DSS application.

## Directory Structure

```
db/
├── mongo/              # MongoDB scripts
│   ├── init.js        # Database initialization
│   └── sample_data.js # Sample data insertion
├── mysql/             # MySQL scripts (alternative)
│   └── schema_mysql.sql
├── etl/               # ETL tools
│   └── etl_csv_loader.py
└── README.md
```

## MongoDB Setup

### Initialize Database

```bash
# Connect to MongoDB
mongo

# Run initialization script
load('db/mongo/init.js')

# Load sample data
load('db/mongo/sample_data.js')
```

### Using Docker

```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

## MySQL Setup (Alternative)

### Initialize Database

```bash
# Connect to MySQL
mysql -u root -p

# Run schema script
source db/mysql/schema_mysql.sql
```

### Using Docker

```bash
docker run -d -p 3306:3306 --name mysql \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=dss_db \
  mysql:latest
```

## ETL Data Loading

### Load CSV Data

```bash
# Install dependencies
pip install pandas pymongo mysql-connector-python

# Load data into MongoDB
python db/etl/etl_csv_loader.py --db-type mongo \
  --customers data/customers.csv \
  --products data/products.csv \
  --orders data/orders.csv

# Load data into MySQL
python db/etl/etl_csv_loader.py --db-type mysql \
  --customers data/customers.csv \
  --products data/products.csv \
  --orders data/orders.csv
```

## Collections/Tables

### MongoDB Collections

- `users` - User accounts
- `customers` - Customer information
- `products` - Product catalog
- `orders` - Order transactions
- `rule_cache` - Cached ML model rules

### MySQL Tables

- `users` - User accounts
- `customers` - Customer information
- `products` - Product catalog
- `orders` - Order transactions
- `order_items` - Order line items
- `rule_cache` - Cached ML model rules

## Configuration

Update database connection settings in:
- Spring Boot: `src/main/resources/application.yml`
- Python Service: Environment variables or config file

