# Deployment Checklist - Return-Risk Gatekeeping Policy

## Pre-Deployment Checklist

### ✅ Code Complete
- [x] All Java classes implemented
- [x] All Python modules implemented  
- [x] All DTOs created
- [x] All repositories created
- [x] All services implemented
- [x] All API endpoints functional
- [x] Frontend dashboard complete
- [x] JavaScript interactivity working

### ✅ Documentation
- [x] Use case diagram created
- [x] README documentation written
- [x] Quick start guide created
- [x] API endpoints documented
- [x] Implementation summary completed

### ⚠️ Testing (Recommended)
- [ ] Unit tests for ReturnRiskService
- [ ] Unit tests for ReturnRiskPipeline (Python)
- [ ] Integration tests for API endpoints
- [ ] End-to-end UI tests
- [ ] Load testing with 10,000+ orders
- [ ] Browser compatibility testing

### ⚠️ Data Integration
- [ ] Replace mock data with real order history
- [ ] Implement actual customer return rate calculation
- [ ] Implement actual SKU return rate calculation
- [ ] Set up MongoDB indexes for performance
- [ ] Configure data retention policies

### ⚠️ Security
- [ ] Add authentication to API endpoints
- [ ] Implement role-based access control
- [ ] Add audit logging for policy changes
- [ ] Validate all user inputs
- [ ] Add CSRF protection

### ⚠️ Configuration
- [ ] Set production model service URL
- [ ] Configure MongoDB connection string
- [ ] Set appropriate COGS/cost defaults
- [ ] Configure logging levels
- [ ] Set up environment variables

---

## Deployment Steps

### 1. Database Setup

```bash
# Production MongoDB
# Option 1: Docker
docker run -d \
  --name dss-mongo-prod \
  -p 27017:27017 \
  -v /data/mongodb:/data/db \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=<secure-password> \
  mongo:latest

# Option 2: Cloud (MongoDB Atlas)
# 1. Create cluster at cloud.mongodb.com
# 2. Get connection string
# 3. Update application.yml
```

### 2. Python Model Service Deployment

```bash
cd model-service

# Install dependencies
pip install -r requirements.txt

# Option 1: Run as service (systemd)
sudo nano /etc/systemd/system/dss-model-service.service
```

```ini
[Unit]
Description=DSS Model Service
After=network.target

[Service]
User=dss
WorkingDirectory=/opt/dss/model-service
ExecStart=/usr/bin/python3 app.py
Restart=always

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl start dss-model-service
sudo systemctl enable dss-model-service

# Option 2: Docker
docker build -t dss-model-service .
docker run -d -p 8000:8000 dss-model-service

# Option 3: Cloud (AWS/GCP/Azure)
# Deploy using platform-specific tools
```

### 3. Spring Boot Application Deployment

```bash
# Build JAR
./mvnw clean package

# Run as service (systemd)
sudo nano /etc/systemd/system/dss-app.service
```

```ini
[Unit]
Description=DSS Application
After=network.target

[Service]
User=dss
WorkingDirectory=/opt/dss
ExecStart=/usr/bin/java -jar target/dss-0.0.1-SNAPSHOT.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl start dss-app
sudo systemctl enable dss-app

# Or deploy to cloud platform
# AWS Elastic Beanstalk, GCP App Engine, etc.
```

### 4. Nginx Reverse Proxy (Optional but Recommended)

```nginx
server {
    listen 80;
    server_name dss.yourcompany.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /model-service/ {
        proxy_pass http://localhost:8000/;
        proxy_set_header Host $host;
    }
}
```

### 5. SSL/TLS Setup

```bash
# Using Let's Encrypt
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d dss.yourcompany.com
```

---

## Post-Deployment Verification

### ✅ Health Checks

```bash
# 1. Check MongoDB
mongosh --eval "db.adminCommand('ping')"

# 2. Check Model Service
curl http://localhost:8000
# Expected: {"status": "ok", "service": "DSS Model Service"}

# 3. Check Spring Boot
curl http://localhost:8080/actuator/health
# Expected: {"status": "UP"}

# 4. Check API endpoints
curl http://localhost:8080/api/policy/optimize \
  -X POST -H "Content-Type: application/json" \
  -d '{"sampleSize": 100}'
```

### ✅ Smoke Tests

1. **Access Dashboard**: http://localhost:8080/sales/policy
2. **Run Simulation**: Click "Run Simulation"
3. **Find Optimal**: Click "Find Optimal τ*"
4. **Verify Charts**: Ensure profit curve displays
5. **Adjust Slider**: Move threshold, verify real-time updates

---

## Monitoring Setup

### Application Monitoring

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### Logging

```yaml
logging:
  level:
    com.g5.dss: INFO
  file:
    name: /var/log/dss/application.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

### Alerts to Configure

1. **API Response Time** > 5 seconds
2. **Model Service Down** (health check fails)
3. **MongoDB Connection Lost**
4. **High Error Rate** > 5%
5. **Profit Calculation Failures**

---

## Rollback Plan

### If Issues Arise

1. **Stop New Deployment**:
   ```bash
   sudo systemctl stop dss-app
   sudo systemctl stop dss-model-service
   ```

2. **Restore Previous Version**:
   ```bash
   cd /opt/dss/backups
   sudo cp dss-v1.0.jar /opt/dss/target/dss-0.0.1-SNAPSHOT.jar
   ```

3. **Restart Services**:
   ```bash
   sudo systemctl start dss-model-service
   sudo systemctl start dss-app
   ```

4. **Verify Rollback**:
   - Check application loads
   - Test critical features
   - Monitor logs for errors

---

## Data Migration (If Needed)

### Migrate Existing Policies

```javascript
// MongoDB script
db.policy_configurations.insertMany([
  {
    policyName: "Default Production Policy",
    optimalThreshold: 75.0,
    returnProcessingCost: 15.0,
    conversionRateImpact: 0.2,
    isActive: true,
    isDefault: true,
    createdAt: new Date(),
    createdBy: "admin"
  }
]);
```

---

## Performance Tuning

### MongoDB Indexes

```javascript
// Create indexes for better query performance
db.return_risk_scores.createIndex({ orderId: 1 });
db.return_risk_scores.createIndex({ customerId: 1 });
db.return_risk_scores.createIndex({ scoredAt: -1 });
db.return_risk_scores.createIndex({ riskLevel: 1 });
db.policy_configurations.createIndex({ isActive: 1 });
```

### JVM Tuning

```bash
# For production
java -Xms2g -Xmx4g -jar dss-0.0.1-SNAPSHOT.jar
```

### Python Workers

```bash
# For high-traffic environments
uvicorn app:app --workers 4 --host 0.0.0.0 --port 8000
```

---

## User Training

### Materials to Prepare

1. **User Manual**: How to use the dashboard
2. **Video Tutorial**: 5-minute walkthrough
3. **FAQ Document**: Common questions
4. **Training Session**: 30-minute live demo

### Key Points to Cover

- How to find optimal threshold
- What each KPI means
- How to interpret profit curve
- When to adjust business parameters
- How to deploy policies

---

## Go-Live Checklist

### Day Before Launch

- [ ] All tests passed
- [ ] Backups created
- [ ] Monitoring configured
- [ ] Alerts set up
- [ ] Documentation finalized
- [ ] Training materials ready
- [ ] Support team briefed

### Launch Day

- [ ] Deploy model service
- [ ] Deploy Spring Boot app
- [ ] Run health checks
- [ ] Execute smoke tests
- [ ] Monitor logs (first 2 hours)
- [ ] Get user feedback

### First Week

- [ ] Daily monitoring
- [ ] Collect user feedback
- [ ] Track conversion rates
- [ ] Compare predicted vs. actual returns
- [ ] Fine-tune parameters if needed

---

## Support Plan

### Level 1: User Issues
- Dashboard not loading
- Charts not displaying
- Slider not working
→ Check browser console, verify connectivity

### Level 2: API Errors
- Simulation failed
- Optimization timeout
- Data inconsistency
→ Check logs, restart services

### Level 3: System Failures
- Model service down
- MongoDB crash
- Corrupted data
→ Execute rollback, contact development team

---

## Success Metrics (First Month)

Track these KPIs:

1. **Profit Improvement**: Target +5% vs. baseline
2. **Return Rate Reduction**: Target -10%
3. **Conversion Impact**: Monitor actual vs. predicted
4. **User Adoption**: % of inventory managers using feature
5. **System Uptime**: Target 99.9%
6. **Average Response Time**: Target <2 seconds

---

## Continuous Improvement

### Monthly Reviews

- [ ] Analyze profit protected
- [ ] Compare forecasts vs. actuals
- [ ] Adjust model weights if needed
- [ ] Review user feedback
- [ ] Plan feature enhancements

### Quarterly Updates

- [ ] Retrain ML model with new data
- [ ] Update business parameter defaults
- [ ] Add requested features
- [ ] Performance optimization

---

**Ready for Production? Let's launch! 🚀**

Last Updated: October 27, 2025
