// MongoDB Initialization Script
// Run this script to initialize the DSS database

use dss_db;

// Create collections
db.createCollection('users');
db.createCollection('customers');
db.createCollection('products');
db.createCollection('orders');
db.createCollection('rule_cache');

// Create indexes
db.users.createIndex({ 'username': 1 }, { unique: true });
db.users.createIndex({ 'email': 1 }, { unique: true });

db.customers.createIndex({ 'customerId': 1 }, { unique: true });
db.customers.createIndex({ 'email': 1 });
db.customers.createIndex({ 'segment': 1 });

db.products.createIndex({ 'productId': 1 }, { unique: true });
db.products.createIndex({ 'category': 1 });

db.orders.createIndex({ 'orderId': 1 }, { unique: true });
db.orders.createIndex({ 'customerId': 1 });
db.orders.createIndex({ 'orderDate': -1 });
db.orders.createIndex({ 'status': 1 });

db.rule_cache.createIndex({ 'ruleType': 1 });
db.rule_cache.createIndex({ 'expiresAt': 1 });

// Insert sample admin user
db.users.insertOne({
    username: 'admin',
    password: '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', // admin123
    email: 'admin@dss.com',
    role: 'ADMIN',
    enabled: true
});

print('Database initialized successfully!');

