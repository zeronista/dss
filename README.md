# Decision Support System (DSS)

A comprehensive Decision Support System for retail business intelligence, featuring customer segmentation, sales policy optimization, inventory management, and staff support tools.

## 🚀 Features

### 📊 Marketing Analytics
- **RFM Analysis**: Customer segmentation based on Recency, Frequency, and Monetary values
- **Association Rules**: Product recommendation and cross-selling using machine learning
- **Customer Insights**: Actionable recommendations for different customer segments

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

- **[Architecture Overview](docs/architecture.md)** - System design and components
- **[Deployment Guide](docs/deployment.md)** - Production deployment instructions
- **[API Reference](docs/api_reference.md)** - Complete API documentation

## 🔧 Configuration

### Spring Boot Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: dss_db

model-service:
  base-url: http://localhost:8000

server:
  port: 8080
```

### Model Service Configuration

Set environment variables:
```bash
export DB_CONNECTION_STRING=mongodb://localhost:27017/
export PORT=8000
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

### Dashboard
- Real-time business metrics
- Sales trends visualization
- Category distribution charts
- Quick access to all modules

### Marketing Segments
- RFM customer segmentation
- Segment-specific recommendations
- Customer lifetime value analysis

### Sales Policy
- Return risk prediction
- Policy simulation tools
- Order approval recommendations

### Inventory Audit
- Automated anomaly detection
- Stock level optimization
- Comprehensive audit reports

### Staff Tools
- Cross-sell recommendations
- Performance tracking
- Customer insights

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is part of academic coursework for DSS301 at FPT University.

## 👥 Team

Group 5 (G5) - DSS301

## 📞 Support

For issues and questions:
- Check the [documentation](docs/)
- Review [API Reference](docs/api_reference.md)
- Contact the development team

## 🎯 Roadmap

- [ ] Implement actual ML models for RFM analysis
- [ ] Add Apriori/FP-Growth algorithms
- [ ] Train return risk prediction model
- [ ] Implement Isolation Forest for anomalies
- [ ] Add real-time notifications
- [ ] Implement advanced caching strategies
- [ ] Add comprehensive unit tests
- [ ] Create mobile-responsive dashboard
- [ ] Add export functionality for reports
- [ ] Implement role-based dashboards

## 🙏 Acknowledgments

- Spring Boot community
- FastAPI framework
- Chart.js visualization library
- Bootstrap UI framework

