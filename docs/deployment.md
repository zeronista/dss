# Deployment Guide

## Prerequisites

- Java 17 or higher
- Python 3.8 or higher
- MongoDB 6.0+ OR MySQL 8.0+
- Maven 3.6+

## Local Development Deployment

### 1. Database Setup

#### MongoDB Option
```bash
# Using Docker
docker run -d --name mongodb \
  -p 27017:27017 \
  -v mongodb_data:/data/db \
  mongo:latest

# Initialize database
mongo < db/mongo/init.js
mongo < db/mongo/sample_data.js
```

#### MySQL Option
```bash
# Using Docker
docker run -d --name mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=dss_db \
  -v mysql_data:/var/lib/mysql \
  mysql:latest

# Initialize database
mysql -u root -p < db/mysql/schema_mysql.sql
```

### 2. Model Service Deployment

```bash
cd model-service

# Create virtual environment
python -m venv venv

# Activate virtual environment
# Windows
venv\Scripts\activate
# Linux/Mac
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Run service
python app.py
```

### 3. Spring Boot Application

```bash
# Build the application
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# OR run the JAR directly
java -jar target/dss-0.0.1-SNAPSHOT.jar
```

### 4. Access Application

- Web Application: http://localhost:8080
- Login: admin / admin123
- Model Service API Docs: http://localhost:8000/docs

## Production Deployment

### Using Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    environment:
      - MONGO_INITDB_DATABASE=dss_db

  model-service:
    build: ./model-service
    ports:
      - "8000:8000"
    depends_on:
      - mongodb
    environment:
      - DB_CONNECTION_STRING=mongodb://mongodb:27017/

  spring-app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mongodb
      - model-service
    environment:
      - SPRING_DATA_MONGODB_HOST=mongodb
      - MODEL_SERVICE_BASE_URL=http://model-service:8000

volumes:
  mongodb_data:
```

Deploy:
```bash
docker-compose up -d
```

### Cloud Deployment (AWS Example)

#### 1. Deploy Database
- **MongoDB Atlas** (recommended) or **AWS DocumentDB**
- **Amazon RDS** for MySQL

#### 2. Deploy Model Service
```bash
# Using AWS Elastic Beanstalk or ECS
eb init -p python-3.8 dss-model-service
eb create dss-model-env
eb deploy
```

#### 3. Deploy Spring Boot Application
```bash
# Package as JAR
./mvnw clean package

# Deploy to AWS Elastic Beanstalk
eb init -p java dss-app
eb create dss-app-env
eb deploy
```

#### 4. Configure Environment Variables

**Model Service**:
```bash
export DB_CONNECTION_STRING=mongodb+srv://user:pass@cluster.mongodb.net/dss_db
export PORT=8000
```

**Spring Boot**:
```bash
export SPRING_DATA_MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/dss_db
export MODEL_SERVICE_BASE_URL=http://model-service.amazonaws.com:8000
export SERVER_PORT=8080
```

### Kubernetes Deployment

Create Kubernetes manifests:

**mongodb-deployment.yaml**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mongodb
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mongodb
  template:
    metadata:
      labels:
        app: mongodb
    spec:
      containers:
      - name: mongodb
        image: mongo:latest
        ports:
        - containerPort: 27017
        volumeMounts:
        - name: mongodb-storage
          mountPath: /data/db
      volumes:
      - name: mongodb-storage
        persistentVolumeClaim:
          claimName: mongodb-pvc
```

**spring-app-deployment.yaml**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: spring-app
  template:
    metadata:
      labels:
        app: spring-app
    spec:
      containers:
      - name: spring-app
        image: your-registry/dss-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATA_MONGODB_HOST
          value: mongodb-service
        - name: MODEL_SERVICE_BASE_URL
          value: http://model-service:8000
```

Deploy:
```bash
kubectl apply -f k8s/
```

## Configuration

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
spring:
  data:
    mongodb:
      host: ${MONGODB_HOST:localhost}
      port: ${MONGODB_PORT:27017}
      database: ${MONGODB_DATABASE:dss_db}

model-service:
  base-url: ${MODEL_SERVICE_URL:http://localhost:8000}

server:
  port: ${SERVER_PORT:8080}
```

### Security Configuration

For production, update:
1. Change default admin password
2. Configure HTTPS/SSL
3. Set up proper CORS policies
4. Enable rate limiting
5. Configure firewall rules

## Monitoring

### Application Monitoring

Add Spring Boot Actuator:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Endpoints:
- `/actuator/health` - Health check
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application info

### Database Monitoring

- MongoDB: Use MongoDB Compass or Cloud Manager
- MySQL: Use MySQL Workbench or phpMyAdmin

### Log Management

Configure logging in `application.yml`:

```yaml
logging:
  level:
    root: INFO
    com.g5.dss: DEBUG
  file:
    name: logs/dss-application.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## Backup and Recovery

### Database Backup

**MongoDB**:
```bash
mongodump --host localhost --port 27017 --db dss_db --out /backup/
mongorestore --host localhost --port 27017 --db dss_db /backup/dss_db/
```

**MySQL**:
```bash
mysqldump -u root -p dss_db > backup.sql
mysql -u root -p dss_db < backup.sql
```

## Performance Tuning

1. **Database Indexes**: Ensure proper indexes are created
2. **Connection Pooling**: Configure optimal pool sizes
3. **Caching**: Enable Spring Cache for frequently accessed data
4. **JVM Tuning**: Set appropriate heap sizes
5. **Load Balancing**: Use Nginx or AWS ELB for multiple instances

## Troubleshooting

### Common Issues

1. **Port already in use**
   ```bash
   # Change port in application.yml
   server.port: 8081
   ```

2. **Cannot connect to database**
   - Check database is running
   - Verify connection string
   - Check firewall rules

3. **Model service timeout**
   - Increase timeout in RestTemplate configuration
   - Check model service logs
   - Verify network connectivity

### Health Checks

```bash
# Check Spring Boot health
curl http://localhost:8080/actuator/health

# Check Model Service health
curl http://localhost:8000/

# Check Database connection
mongo --eval "db.runCommand({ping: 1})"
```

## Support

For issues and support:
- Check logs in `logs/dss-application.log`
- Review documentation in `docs/`
- Contact development team

