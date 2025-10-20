# System Architecture

## Overview

The DSS (Decision Support System) is a multi-tier application designed to provide business intelligence and decision support capabilities for retail operations.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend/UI Layer                     │
│                  (Thymeleaf Templates + JS)                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Spring Boot Application                    │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐  │
│  │  Controllers  │  │  REST APIs    │  │   Services    │  │
│  │  (MVC Views)  │  │  (JSON APIs)  │  │  (Business)   │  │
│  └───────────────┘  └───────────────┘  └───────────────┘  │
│         │                   │                   │            │
│         └───────────────────┴───────────────────┘            │
│                              │                                │
│                    ┌─────────┴─────────┐                    │
│                    │                   │                    │
│            ┌───────▼──────┐    ┌──────▼────────┐          │
│            │ Repositories │    │   RestTemplate │          │
│            │ (Data Access)│    │  (HTTP Client) │          │
│            └───────┬──────┘    └──────┬────────┘          │
└────────────────────┼───────────────────┼───────────────────┘
                     │                   │
                     ▼                   ▼
         ┌──────────────────┐  ┌──────────────────┐
         │   MongoDB/MySQL  │  │  Python FastAPI  │
         │   (Primary DB)   │  │  (Model Service) │
         └──────────────────┘  └──────────────────┘
                                         │
                                         ▼
                               ┌──────────────────┐
                               │  ML Pipelines    │
                               │  - RFM           │
                               │  - Rules         │
                               │  - Risk Predict  │
                               │  - Anomaly       │
                               └──────────────────┘
```

## Components

### 1. Spring Boot Server (Port 8080)

**Purpose**: Main application server handling web requests and business logic

**Key Packages**:
- `controller/` - MVC controllers serving HTML views
- `api/` - REST controllers serving JSON for AJAX/Chart.js
- `service/` - Business logic layer
- `repository/` - Data access layer (Spring Data)
- `domain/` - Entity models
- `dto/` - Data Transfer Objects
- `config/` - Configuration (Security, Web, Database)
- `util/` - Utility classes

### 2. Model Service (Python FastAPI, Port 8000)

**Purpose**: Machine learning and analytics service

**Pipelines**:
- **RFM Pipeline**: Customer segmentation (Recency, Frequency, Monetary)
- **Rules Pipeline**: Association rule mining (Apriori/FP-Growth)
- **Return Risk Pipeline**: ML-based return prediction
- **Anomaly Pipeline**: Inventory anomaly detection

### 3. Database Layer

**Options**:
- **MongoDB** (NoSQL) - Flexible schema, document-based
- **MySQL** (SQL) - Relational, ACID compliant

**Collections/Tables**:
- `users` - Authentication and authorization
- `customers` - Customer information and segments
- `products` - Product catalog
- `orders` - Transaction records
- `rule_cache` - Cached ML results

### 4. Frontend Layer

**Technologies**:
- **Thymeleaf** - Server-side template engine
- **Bootstrap 5** - Responsive UI framework
- **Chart.js** - Data visualization
- **Vanilla JS** - Client-side interactivity

## Data Flow

### Example: RFM Analysis Flow

1. User accesses `/marketing/segments` (Controller)
2. Controller returns `marketing_segments.html` (View)
3. JavaScript calls `/api/marketing/rfm-segments` (REST API)
4. REST API calls `MarketingService.getRfmSegments()`
5. Service calls Python Model Service via `RestTemplate`
6. Model Service performs RFM analysis
7. Results flow back: Model → Service → API → JavaScript → UI

## Security

- **Spring Security** for authentication/authorization
- **Password Encryption** using BCrypt
- **CORS** enabled for API endpoints
- **Session Management** with remember-me support

## Scalability Considerations

- **Horizontal Scaling**: Spring Boot can be deployed as multiple instances behind a load balancer
- **Caching**: Rule cache reduces ML computation overhead
- **Async Processing**: Long-running ML tasks can be made asynchronous
- **Database Sharding**: MongoDB supports sharding for large datasets

## Technology Stack

### Backend
- Java 17+
- Spring Boot 3.x
- Spring Data (MongoDB/JPA)
- Spring Security

### Model Service
- Python 3.8+
- FastAPI
- Pandas, NumPy
- Scikit-learn
- MLxtend (for association rules)

### Frontend
- Thymeleaf
- Bootstrap 5
- Chart.js 4.x
- Vanilla JavaScript

### Database
- MongoDB 6.0+ OR MySQL 8.0+

## Configuration

### Spring Boot (`application.yml`)
```yaml
server.port: 8080
spring.data.mongodb.host: localhost
spring.data.mongodb.port: 27017
model-service.base-url: http://localhost:8000
```

### Model Service
```bash
PORT=8000
DB_CONNECTION_STRING=mongodb://localhost:27017/
```

## Development Setup

1. **Start Database**
   ```bash
   # MongoDB
   docker run -d -p 27017:27017 mongo:latest
   
   # OR MySQL
   docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password mysql:latest
   ```

2. **Start Model Service**
   ```bash
   cd model-service
   pip install -r requirements.txt
   python app.py
   ```

3. **Start Spring Boot**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access Application**
   - Main App: http://localhost:8080
   - Model Service API: http://localhost:8000/docs

## Deployment

See `deployment.md` for production deployment instructions.

