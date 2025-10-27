# Decision Support System (DSS)

A comprehensive Decision Support System for retail business intelligence with advanced customer segmentation, market basket analysis, sales policy optimization, and real-time analytics dashboard.

## 🚀 Features

### 📊 Marketing Segments (Customer Segmentation)
- **RFM Analysis**: Automated customer segmentation into 5 groups (Champions, Loyal, At-Risk, Hibernating, Regulars)
- **Customer Profiling**: Detailed metrics including Recency, Frequency, and Monetary analysis
- **Segment-specific Strategies**: Tailored marketing actions for each customer segment
- **Export Functionality**: Download customer lists as CSV for campaigns
- **Interactive Dashboard**: Visual representation with Chart.js doughnut charts

### 🛒 Market Basket Analysis
- **Association Rules Mining**: Discover product combinations frequently bought together
- **Support & Confidence Metrics**: Identify strong product relationships
- **Lift Analysis**: Measure correlation strength between products
- **Bundle Recommendations**: Automated suggestions for product bundling (🔥 Excellent, ✅ Good, 💡 Fair)
- **Segment-based Analysis**: Filter rules by customer segments
- **Visual Analytics**: Top 10 rules chart with dual-axis visualization

### 📈 Dashboard & Analytics
- **Real-time Metrics**: Total customers, orders, revenue, active products
- **Interactive Charts**: Sales trends, top products, category distribution
- **MySQL Integration**: Fast analytics with 541K+ retail transactions
- **MongoDB Support**: Flexible document-based data storage
- **Chart.js Visualizations**: Modern, responsive charts (bar, doughnut, line)

### 💼 Sales Policy Management
- **Return Risk Prediction**: ML-based prediction for sales return likelihood
- **Policy Simulation**: Test different sales policies before implementation
- **Decision Support**: Data-driven recommendations for order approval

### 📦 Inventory Management
- **Anomaly Detection**: Automated detection of inventory irregularities
- **Audit Reports**: Comprehensive inventory audit and analysis
- **Stock Optimization**: Insights for optimal stock levels

### 👥 Staff Support
- **Cross-sell Recommendations**: AI-powered product suggestions for customers
- **Performance Metrics**: Track staff effectiveness and sales performance
- **Real-time Insights**: Access to customer and product data

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Spring Boot Server (8080)                 │
│  Controllers → Services → Repositories → Database            │
│       ↓           ↓                                          │
│   Views (HTML) + REST APIs (JSON)                           │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│            Python FastAPI Model Service (8000)               │
│  RFM | Association Rules | Risk Prediction | Anomaly        │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    MongoDB / MySQL Database                  │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Technology Stack

### Backend
- **Java 17+** with Spring Boot 3.x
- **Spring Data** (MongoDB/JPA)
- **Spring Security** for authentication
- **Maven** for dependency management

### Model Service
- **Python 3.8+** with FastAPI
- **Pandas & NumPy** for data processing
- **Scikit-learn** for machine learning
- **MLxtend** for association rule mining

### Frontend
- **Thymeleaf** template engine
- **Bootstrap 5** for responsive UI
- **Chart.js** for data visualization
- **Vanilla JavaScript** for interactivity

## 🗄️ Database

### Data Overview
- **Total Transactions**: 541,909 records (online_retail table)
- **Unique Customers**: 4,372 customers
- **Unique Products**: 4,070 SKUs
- **Countries**: 38 countries
- **Date Range**: December 2010 - December 2011
- **Source**: [UCI ML Repository - Online Retail Dataset](https://archive.ics.uci.edu/ml/datasets/online+retail)

### Schema

**MySQL** (`dss_db_mysql`):
```sql
CREATE TABLE online_retail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_no VARCHAR(20),
    stock_code VARCHAR(50),
    description TEXT,
    quantity INT,
    invoice_date DATETIME,
    unit_price DECIMAL(10,2),
    customer_id INT,
    country VARCHAR(100)
);
```

**MongoDB** (`DSS` database):
```javascript
{
  "_id": ObjectId,
  "InvoiceNo": "536365",
  "StockCode": "85123A",
  "Description": "WHITE HANGING HEART T-LIGHT HOLDER",
  "Quantity": 6,
  "InvoiceDate": "12/1/2010 8:26",
  "UnitPrice": 2.55,
  "CustomerID": 17850,
  "Country": "United Kingdom"
}
```

### Database
- **MongoDB 6.0+** (primary option)
- **MySQL 8.0+** (alternative)

## 📁 Project Structure

```
dss/
├── src/main/java/com/g5/dss/
│   ├── config/              # Configuration classes
│   ├── controller/          # MVC controllers (HTML views)
│   ├── api/                 # REST controllers (JSON APIs)
│   ├── service/             # Business logic layer
│   ├── repository/          # Data access layer
│   ├── domain/              # Entity models
│   ├── dto/                 # Data Transfer Objects
│   └── util/                # Utility classes
│
├── src/main/resources/
│   ├── application.yml      # Application configuration
│   ├── static/              # CSS, JS, images
│   └── templates/           # Thymeleaf HTML templates
│
├── model-service/           # Python ML service
│   ├── app.py              # FastAPI application
│   ├── pipelines/          # ML pipelines
│   └── requirements.txt    # Python dependencies
│
├── db/                      # Database scripts
│   ├── mongo/              # MongoDB initialization
│   ├── mysql/              # MySQL schema (alternative)
│   └── etl/                # ETL tools
│
└── docs/                    # Documentation
    ├── architecture.md     # System architecture
    ├── deployment.md       # Deployment guide
    └── api_reference.md    # API documentation
```

## 🚦 Getting Started

### Prerequisites

- Java 17 or higher
- Python 3.8 or higher
- MongoDB 6.0+ OR MySQL 8.0+
- Maven 3.6+

### Quick Start

#### 1. Start Database

**MongoDB (Recommended)**:
```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

**MySQL (Alternative)**:
```bash
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password mysql:latest
```

#### 2. Initialize Database

**MongoDB**:
```bash
mongo < db/mongo/init.js
mongo < db/mongo/sample_data.js
```

**MySQL**:
```bash
mysql -u root -p < db/mysql/schema_mysql.sql
```

#### 3. Start Model Service

```bash
cd model-service
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
python app.py
```

Model service will start on `http://localhost:8000`

#### 4. Start Spring Boot Application

```bash
./mvnw spring-boot:run
```

Application will start on `http://localhost:8080`

#### 5. Access Application

- **Web Interface**: http://localhost:8080
- **Login**: `admin` / `admin123`
- **API Documentation**: http://localhost:8000/docs

## 📖 Documentation

### Quick Guides
- **[SEGMENTATION_GUIDE.md](SEGMENTATION_GUIDE.md)** - Complete guide for Customer Segmentation & Market Basket features
- **[DASHBOARD_GUIDE.md](DASHBOARD_GUIDE.md)** - Dashboard setup and usage
- **[MONGODB_INTEGRATION_GUIDE.md](MONGODB_INTEGRATION_GUIDE.md)** - MongoDB integration guide
- **[TEMPLATES_GUIDE.md](TEMPLATES_GUIDE.md)** - Template details and customization

### Technical Docs
- **[Architecture Overview](docs/architecture.md)** - System design and components
- **[Deployment Guide](docs/deployment.md)** - Production deployment instructions
- **[API Reference](docs/api_reference.md)** - Complete API documentation

### Troubleshooting
- **[SEGMENTATION_FIX.md](SEGMENTATION_FIX.md)** - Fix Thymeleaf template errors
- **[MAPPING_CONFLICT_FIX.md](MAPPING_CONFLICT_FIX.md)** - Resolve URL mapping conflicts
- **[CHART_FIX.md](CHART_FIX.md)** - Chart.js horizontal bar chart fix

## 🔧 Configuration

### Spring Boot Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dss_db_mysql
    username: root
    password: 1234
  
  data:
    mongodb:
      uri: mongodb+srv://user:pass@cluster.mongodb.net/DSS
      database: DSS

model-service:
  base-url: http://localhost:8000
  timeout: 30000

server:
  port: 8080

security:
  admin:
    username: admin
    password: admin123
```

### Environment Variables

Set these for production:

```bash
# Database
export MYSQL_URL=jdbc:mysql://localhost:3306/dss_db_mysql
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=your_password
export MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/DSS
export MONGODB_DATABASE=DSS

# Security
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD=your_secure_password

# Model Service
export MODEL_SERVICE_URL=http://localhost:8000
export MODEL_SERVICE_TIMEOUT=30000
```

## 🌐 API Endpoints

### Segmentation & Market Basket APIs

```http
# Get RFM analysis for all customers
GET /api/segmentation/rfm

# Get segment summary
GET /api/segmentation/summary

# Get at-risk customers
GET /api/segmentation/at-risk

# Get customers in specific segment
GET /api/segmentation/segment/{segmentName}
# Example: /api/segmentation/segment/Champions

# Market basket analysis
POST /api/segmentation/market-basket?segment=Champions&minSupport=1.0&minConfidence=30&maxRules=20

# Product recommendations
GET /api/segmentation/product-recommendations/{stockCode}?segment=Champions&topN=5

# Segmentation statistics
GET /api/segmentation/stats
```

### Dashboard & Report APIs

```http
# Dashboard statistics
GET /api/report/dashboard-stats

# Chart data
GET /api/report/chart-data?type=sales
GET /api/report/chart-data?type=products
```

### Response Examples

**RFM Analysis Response**:
```json
[
  {
    "customerId": 17850,
    "country": "United Kingdom",
    "recency": 15,
    "frequency": 10,
    "monetary": 100000,
    "segment": "Champions",
    "segmentId": 1,
    "lastPurchaseDate": "2024-01-01T00:00:00",
    "avgOrderValue": 10000,
    "totalQuantity": 500
  }
]
```

**Market Basket Response**:
```json
[
  {
    "productACode": "85123A",
    "productAName": "WHITE HANGING HEART T-LIGHT HOLDER",
    "productBCode": "22423",
    "productBName": "REGENCY CAKESTAND 3 TIER",
    "support": 0.025,
    "confidence": 65.5,
    "lift": 2.3,
    "transactionCount": 150,
    "recommendation": "🔥 Bundle mạnh - Tạo combo khuyến mãi"
  }
]
```

## 🧪 Testing

```bash
# Run tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## 📦 Building for Production

```bash
# Package application
./mvnw clean package

# Run JAR
java -jar target/dss-0.0.1-SNAPSHOT.jar
```

## 🐳 Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up -d
```

## 🔐 Security

- **Authentication**: Spring Security with BCrypt password encryption
- **Authorization**: Role-based access control
- **Session Management**: Secure session handling with remember-me support
- **CORS**: Configured for API endpoints

Default credentials:
- Username: `admin`
- Password: `admin123`

**⚠️ Change these credentials in production!**

## 📊 Features Showcase

### 🏠 Dashboard
- **Real-time Metrics**: Total customers (4,372), orders, revenue, active products
- **Sales Trends**: Interactive line charts with Chart.js
- **Top Products**: Horizontal bar chart showing quantity sold
- **Category Distribution**: Pie chart for product categories
- **Quick Navigation**: Direct links to all modules

### 👥 Marketing Segments
- **5 Customer Segments**: 
  - 🏆 **Champions** - VIP customers with high R/F/M
  - 💎 **Loyal** - Consistent returning customers
  - ⚠️ **At-Risk** - Previously active, now declining
  - 😴 **Hibernating** - Inactive for extended periods
  - 👥 **Regulars** - Average engagement customers
- **Segment Analytics**: Customer count, total value, avg R/F/M metrics
- **Marketing Actions**: Tailored strategies for each segment
- **Export Functionality**: Download segment data as CSV
- **RFM Charts**: Visual representation of customer distribution

### 🛒 Market Basket Analysis
- **Association Rules**: Discover product bundles (A → B relationships)
- **Key Metrics**:
  - **Support**: % of transactions with both products
  - **Confidence**: Probability of buying B when buying A
  - **Lift**: Strength of relationship (>2.0 = strong, >1.5 = good)
- **Segment Filtering**: Analyze rules per customer segment
- **Adjustable Thresholds**: Min support, min confidence controls
- **Top Rules Chart**: Dual-axis visualization (Confidence vs Lift)
- **Bundle Recommendations**: 
  - 🔥 Excellent (Conf≥70% + Lift≥2.0)
  - ✅ Good (Conf≥60% + Lift≥1.5)
  - 💡 Fair (Conf≥50% + Lift≥1.0)

### 💼 Sales Policy
- **Return Risk Prediction**: ML-based order risk assessment
- **Policy Simulation**: Test approval thresholds
- **Order Recommendations**: Accept/Review/Reject decisions

### 📦 Inventory Audit
- **Anomaly Detection**: Automated irregularity identification
- **Stock Analysis**: Comprehensive audit reports
- **Optimization Insights**: Recommended stock levels

### 🛍️ Staff Cross-sell
- **Product Recommendations**: AI-powered suggestions
- **Customer Insights**: Real-time customer data access
- **Performance Tracking**: Staff effectiveness metrics

## 📸 Screenshots

### Dashboard
![Dashboard Overview](docs/screenshots/dashboard.png)
- Real-time KPIs and sales trends
- Interactive Chart.js visualizations
- Quick navigation to all modules

### Marketing Segments
![Customer Segmentation](docs/screenshots/segments.png)
- 5 customer segments with RFM analysis
- Segment-specific marketing strategies
- Doughnut chart distribution

### Segment Detail
![Segment Detail](docs/screenshots/segment_detail.png)
- Customer list with ranking (🥇🥈🥉)
- RFM metrics with color-coded badges
- Export to CSV functionality

### Market Basket Analysis
![Market Basket](docs/screenshots/market_basket.png)
- Association rules with Support/Confidence/Lift
- Bundle recommendations (🔥✅💡)
- Top 10 rules dual-axis chart

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Java naming conventions
- Use meaningful variable names
- Add JavaDoc comments for public methods
- Keep methods focused and concise
- Write unit tests for new features

## 📝 License

This project is part of academic coursework for DSS301 at FPT University.

## 👥 Team

**Group 5 (G5) - DSS301**

- Spring Boot Development
- Customer Segmentation Implementation
- Market Basket Analysis
- Dashboard & Analytics
- Documentation

## 📞 Support

For issues and questions:
- 📖 Check the [documentation](SEGMENTATION_GUIDE.md)
- 🔍 Review [API Reference](docs/api_reference.md)
- 🐛 Open an issue on GitHub
- 💬 Contact the development team

## 🙏 Acknowledgments

- **Spring Boot** - Application framework
- **Chart.js** - Data visualization library
- **Bootstrap 5** - UI framework
- **Thymeleaf** - Template engine
- **MongoDB** - Document database
- **MySQL** - Relational database
- **FastAPI** - Python ML service framework
- **FPT University** - Academic support

## 🎯 Roadmap

### ✅ Completed
- [x] MySQL + MongoDB dual database support
- [x] Dashboard with real-time analytics
- [x] RFM customer segmentation (5 segments)
- [x] Market basket analysis with association rules
- [x] Segment detail pages with export CSV
- [x] Interactive charts (Chart.js 4.x)
- [x] Thymeleaf security fix (data-* attributes)
- [x] URL mapping conflict resolution
- [x] Comprehensive documentation (7 guides)

### 🚧 In Progress
- [ ] Python ML service integration (FastAPI)
- [ ] Return risk prediction model training
- [ ] Anomaly detection (Isolation Forest)
- [ ] Cross-sell recommendation engine

### 📋 Planned
- [ ] Real-time notifications
- [ ] Advanced caching strategies
- [ ] Comprehensive unit tests (JUnit 5)
- [ ] Role-based dashboards (Manager/Staff/Admin)
- [ ] Mobile-responsive optimization
- [ ] PDF export for reports
- [ ] Email campaign integration
- [ ] Multi-language support (i18n)

## 🙏 Acknowledgments

- Spring Boot community
- FastAPI framework
- Chart.js visualization library
- Bootstrap UI framework

