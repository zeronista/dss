// MongoDB Sample Data
// Run this script to populate the database with sample data

use dss_db;

// Sample Products
db.products.insertMany([
    {
        productId: 'P001',
        name: 'Laptop',
        category: 'Electronics',
        price: 999.99,
        stockQuantity: 50,
        description: 'High-performance laptop'
    },
    {
        productId: 'P002',
        name: 'Mouse',
        category: 'Electronics',
        price: 29.99,
        stockQuantity: 200,
        description: 'Wireless mouse'
    },
    {
        productId: 'P003',
        name: 'Keyboard',
        category: 'Electronics',
        price: 79.99,
        stockQuantity: 150,
        description: 'Mechanical keyboard'
    },
    {
        productId: 'P004',
        name: 'Monitor',
        category: 'Electronics',
        price: 299.99,
        stockQuantity: 75,
        description: '27-inch monitor'
    },
    {
        productId: 'P005',
        name: 'Desk Chair',
        category: 'Furniture',
        price: 199.99,
        stockQuantity: 30,
        description: 'Ergonomic office chair'
    }
]);

// Sample Customers
db.customers.insertMany([
    {
        customerId: 'C001',
        name: 'John Doe',
        email: 'john.doe@example.com',
        phone: '1234567890',
        address: '123 Main St',
        segment: 'Champions',
        registeredDate: new Date('2023-01-15')
    },
    {
        customerId: 'C002',
        name: 'Jane Smith',
        email: 'jane.smith@example.com',
        phone: '0987654321',
        address: '456 Oak Ave',
        segment: 'Loyal Customers',
        registeredDate: new Date('2023-03-20')
    },
    {
        customerId: 'C003',
        name: 'Bob Johnson',
        email: 'bob.johnson@example.com',
        phone: '5551234567',
        address: '789 Pine Rd',
        segment: 'At Risk',
        registeredDate: new Date('2022-11-10')
    }
]);

// Sample Orders
db.orders.insertMany([
    {
        orderId: 'O001',
        customerId: 'C001',
        orderDate: new Date('2024-01-15'),
        status: 'Completed',
        totalAmount: 1109.97,
        paymentMethod: 'Credit Card',
        items: [
            { productId: 'P001', productName: 'Laptop', quantity: 1, price: 999.99, subtotal: 999.99 },
            { productId: 'P002', productName: 'Mouse', quantity: 1, price: 29.99, subtotal: 29.99 },
            { productId: 'P003', productName: 'Keyboard', quantity: 1, price: 79.99, subtotal: 79.99 }
        ],
        isReturned: false,
        returnRiskScore: 0.15
    },
    {
        orderId: 'O002',
        customerId: 'C002',
        orderDate: new Date('2024-02-20'),
        status: 'Completed',
        totalAmount: 299.99,
        paymentMethod: 'PayPal',
        items: [
            { productId: 'P004', productName: 'Monitor', quantity: 1, price: 299.99, subtotal: 299.99 }
        ],
        isReturned: false,
        returnRiskScore: 0.25
    },
    {
        orderId: 'O003',
        customerId: 'C003',
        orderDate: new Date('2024-03-10'),
        status: 'Returned',
        totalAmount: 199.99,
        paymentMethod: 'Credit Card',
        items: [
            { productId: 'P005', productName: 'Desk Chair', quantity: 1, price: 199.99, subtotal: 199.99 }
        ],
        isReturned: true,
        returnRiskScore: 0.85
    }
]);

print('Sample data inserted successfully!');

