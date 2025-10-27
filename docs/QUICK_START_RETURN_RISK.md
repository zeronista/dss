# Quick Start Guide: Return-Risk Gatekeeping Policy

## 🚀 Get Started in 5 Minutes

### Prerequisites
- Java 17+
- Python 3.8+
- MongoDB (or Docker)
- Maven

---

## Step 1: Start MongoDB

```bash
docker run -d -p 27017:27017 --name dss-mongo mongo:latest
```

---

## Step 2: Start Python Model Service

```bash
cd model-service
pip install -r requirements.txt
python app.py
```

✅ Service running at: http://localhost:8000

Test it:
```bash
curl http://localhost:8000
```

---

## Step 3: Start Spring Boot Application

```bash
./mvnw spring-boot:run
```

✅ Application running at: http://localhost:8080

---

## Step 4: Access the Dashboard

Open browser: **http://localhost:8080/sales/policy**

Default login (if authentication is enabled):
- Username: `admin`
- Password: `admin123`

---

## Step 5: Run Your First Simulation

1. **Find Optimal Threshold**:
   - Click the green "Find Optimal τ*" button
   - Wait for the system to calculate (~5 seconds)
   - See results:
     - **Optimal Threshold**: 75-80 (typical)
     - **Profit Protected**: $2,000-3,000/month
     - **Orders Impacted**: 15-25%

2. **What-If Analysis**:
   - Move the threshold slider left/right
   - Watch the profit curve chart update
   - See orders impacted in real-time

3. **Adjust Business Parameters**:
   - Change "Return Processing Cost" to $20
   - Click "Run Simulation"
   - Observe how optimal τ* shifts

4. **Review Policy Table**:
   - Scroll down to see decision matrix
   - Example:
     - Score 0-75: **APPROVE**
     - Score 75-87: **REQUIRE_PREPAY**
     - Score 87-100: **BLOCK_COD**

---

## 🎯 Understanding the Results

### **KPI Card 1: Optimal Threshold (τ*)**
- **What it is**: The risk score cutoff that maximizes profit
- **Typical range**: 70-85
- **Lower value**: More lenient (approve more orders)
- **Higher value**: Stricter (block more risky orders)

### **KPI Card 2: Expected Profit Protection**
- **What it is**: Extra profit per month vs. approving all orders
- **Typical range**: $1,500-$3,000
- **How**: Saved by preventing high-risk returns OR
            Lost by blocking orders that would convert

### **KPI Card 3: Orders Impacted**
- **What it is**: % of orders requiring gatekeeping action
- **Typical range**: 15-30%
- **Actions**: Require prepayment, block COD, or manual review

---

## 📊 Interpreting the Charts

### **Profit Curve Chart (Left)**
- **X-axis**: Threshold (τ) from 0 to 100
- **Y-axis**: Total expected profit ($)
- **Red dot**: Optimal threshold (τ*)
- **Peak**: Maximum profit point
- **Flat regions**: Insensitive to small τ changes

### **Impact Metrics Chart (Right)**
- **Bar 1**: Number of orders impacted
- **Bar 2**: Revenue at risk from those orders
- **Trade-off**: Higher impact = more protection BUT lower conversion

---

## 🛠️ Common Adjustments

### **Scenario 1: Too Many Orders Blocked**
**Problem**: Orders impacted > 30%  
**Solution**:
1. Increase τ to 85-90
2. Reduce "Conversion Impact" to 15%
3. Click "Run Simulation"

### **Scenario 2: Not Enough Protection**
**Problem**: Profit gain < $1,000  
**Solution**:
1. Decrease τ to 65-70
2. Increase "Return Processing Cost" to $20
3. Click "Find Optimal τ*"

### **Scenario 3: High-Value Customers Affected**
**Problem**: VIP customers getting blocked  
**Solution**:
- (Future feature) Configure customer-tier exceptions
- For now: Set τ higher (80+) to be more lenient

---

## 🎬 Video Tutorial (Conceptual Flow)

1. **[0:00-0:30]** Dashboard overview
2. **[0:30-1:00]** Adjust threshold slider
3. **[1:00-1:30]** Modify business parameters
4. **[1:30-2:00]** Find optimal threshold
5. **[2:00-2:30]** Interpret profit curve
6. **[2:30-3:00]** Review policy table
7. **[3:00-3:30]** Deploy policy

---

## 🐛 Troubleshooting

### **Issue: Python service not responding**
```bash
# Check if running
curl http://localhost:8000

# Restart service
cd model-service
python app.py
```

### **Issue: No data in charts**
1. Check browser console (F12) for errors
2. Verify API connectivity:
   ```bash
   curl http://localhost:8080/api/policy/optimize -X POST \
     -H "Content-Type: application/json" \
     -d '{"sampleSize": 100}'
   ```
3. Check Spring Boot logs for errors

### **Issue: "Simulation failed" error**
1. Ensure all parameters are valid numbers
2. Check MongoDB is running:
   ```bash
   docker ps | grep mongo
   ```
3. Restart Spring Boot application

---

## 📚 Next Steps

1. **Read Full Documentation**: `docs/RETURN_RISK_POLICY_README.md`
2. **Explore Use Cases**: `docs/usecase_inventory_manager.drawio`
3. **Test with Real Data**: Replace mock data with actual orders
4. **Deploy to Production**: Set up A/B testing framework

---

## 🆘 Get Help

- **API Docs**: http://localhost:8000/docs (FastAPI auto-generated)
- **Sample Data**: Check `db/sample_data.js` for test orders
- **Logs**: Check console output for debugging

---

## 💡 Pro Tips

1. **Start conservative**: Begin with high τ (80+), then gradually lower
2. **Monitor conversion**: Track actual conversion rate after policy changes
3. **Seasonal adjustment**: Adjust parameters for holidays/peak season
4. **Country-specific**: Different τ* for different countries
5. **Save configurations**: Export policy settings for backup

---

**Ready to optimize your return-risk policy? Let's go! 🚀**
