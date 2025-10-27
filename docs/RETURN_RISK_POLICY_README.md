# Return-Risk Gatekeeping Policy – Prescriptive Decisions

## Overview

This feature implements a **prescriptive decision support system** for inventory managers to optimize return-risk gatekeeping policies. It provides data-driven recommendations for order approval/rejection thresholds to maximize expected profit while managing return risks.

## 📊 Feature Highlights

### **Prescriptive Analytics**
- **Optimal Threshold (τ*)**: Automatically calculates the best risk threshold to maximize total expected profit
- **What-If Simulation**: Interactive threshold adjustment with real-time profit impact visualization
- **Decision Matrix**: Automated policy rules mapping risk scores to recommended actions

### **Key Components**
1. **Risk Scoring Model** (0-100 scale)
2. **Expected Profit Calculator** (per-order and aggregate)
3. **Threshold Optimizer** (finds τ* that maximizes profit)
4. **Interactive Dashboard** (sliders, charts, KPI cards)

---

## 🎯 Business Objective

**Maximize expected profit** by:
- Identifying high-risk return orders before approval
- Recommending optimal gatekeeping actions (Approve, Require Prepay, Block COD)
- Balancing return losses vs. conversion impact from stricter policies

---

## 🏗️ Architecture

### **Components**

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend (HTML/JS)                    │
│  - Threshold slider (τ)                                 │
│  - Business parameter inputs                             │
│  - Chart.js visualizations                               │
│  - Real-time simulation updates                          │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│              Spring Boot Backend (Java)                  │
│  - PolicyApi: REST endpoints                             │
│  - ReturnRiskService: Business logic                     │
│  - Repositories: MongoDB persistence                     │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│            Python Model Service (FastAPI)                │
│  - ReturnRiskPipeline: ML algorithms                     │
│  - Risk scoring: weighted features                       │
│  - Profit calculation: expected value analysis           │
│  - Threshold optimization: sweep algorithm               │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 File Structure

### **Backend (Spring Boot)**
- `domain/mongo/ReturnRiskScore.java` - Risk assessment entity
- `domain/mongo/PolicyConfiguration.java` - Policy settings entity
- `dto/OrderRiskRequest.java` - Single order assessment request
- `dto/PolicySimulationRequest.java` - What-if simulation request
- `dto/RiskAssessmentResponse.java` - Risk scoring response
- `dto/OptimalThresholdResponse.java` - τ* optimization response
- `repository/mongo/ReturnRiskScoreRepository.java` - Risk data access
- `repository/mongo/PolicyConfigurationRepository.java` - Policy data access
- `service/ReturnRiskService.java` - Business logic layer
- `api/PolicyApi.java` - REST API endpoints
- `controller/SalesPolicyController.java` - MVC controller

### **Model Service (Python)**
- `model-service/pipelines/return_risk.py` - ML pipeline implementation
- `model-service/app.py` - FastAPI endpoints

### **Frontend**
- `templates/sales_policy.html` - Dashboard UI
- `templates/fragments/sidebar.html` - Navigation (updated)

---

## 🔬 Model Details

### **Risk Scoring Formula**

```python
Risk_Score = α·Customer_Return_Rate + β·SKU_Return_Rate + γ·Is_First_Time − δ·Order_Value
```

**Features:**
- `Customer_Return_Rate`: Historical return frequency (0-1)
- `SKU_Return_Rate`: Product category return rate (0-1)
- `Is_First_Time_Customer`: Boolean (0/1)
- `Order_Value`: Total order amount ($)

**Weights (default):**
- α = 40.0
- β = 35.0
- γ = 15.0
- δ = 0.05

**Output:** Calibrated to 0-100 scale

### **Expected Profit Calculation**

#### **Scenario 1: Approve (Score < τ)**
```python
Expected_Profit = (Revenue − COGS − Shipping) − (Risk_Prob × Return_Cost)
```

#### **Scenario 2: Block/Restrict (Score ≥ τ)**
```python
Expected_Profit = (Revenue − COGS − Shipping) × (1 − Conversion_Impact)
```

**Where:**
- `Risk_Prob = Score / 100`
- `Conversion_Impact` = % of orders lost when policy is applied

### **Optimal Threshold (τ*)**

**Algorithm:**
1. Sweep τ from 0 to 100 (step = 1)
2. For each τ, calculate `Total_Expected_Profit` across all orders
3. Select τ* with maximum profit
4. Generate policy decision matrix

**Output:**
- Optimal threshold value
- Expected profit gain vs. baseline
- Sensitivity analysis
- Policy rules mapping

---

## 📋 API Endpoints

### **1. Assess Single Order Risk**
```http
POST /api/policy/assess-risk
```

**Request:**
```json
{
  "orderId": "ORD12345",
  "customerId": "CUST001",
  "stockCode": "SKU789",
  "quantity": 2,
  "unitPrice": 49.99,
  "country": "United Kingdom"
}
```

**Response:**
```json
{
  "orderId": "ORD12345",
  "riskScore": 68.5,
  "riskLevel": "MEDIUM",
  "recommendedAction": "REQUIRE_PREPAY",
  "actionReason": "Medium-High risk - Require prepayment",
  "expectedProfitIfApproved": 23.45,
  "expectedProfitIfBlocked": 28.00,
  "profitDifference": 4.55,
  "features": { ... },
  "thresholdUsed": 75.0
}
```

### **2. Simulate Policy**
```http
POST /api/policy/simulate
```

**Request:**
```json
{
  "threshold": 75.0,
  "returnProcessingCost": 15.0,
  "conversionRateImpact": 20.0,
  "cogsRatio": 60.0,
  "shippingCostDefault": 5.0,
  "sampleSize": 1000
}
```

**Response:**
```json
{
  "threshold": 75.0,
  "totalExpectedProfit": 45678.90,
  "totalOrders": 1000,
  "ordersImpacted": 234,
  "ordersImpactedPct": 23.4,
  "revenueAtRisk": 12456.78
}
```

### **3. Find Optimal Threshold**
```http
POST /api/policy/optimize
```

**Request:** Same as simulate (without threshold)

**Response:**
```json
{
  "optimalThreshold": 78.0,
  "maxExpectedProfit": 47890.12,
  "profitProtectedPerMonth": 2211.22,
  "recommendation": "Set threshold to 78 to protect $2,211.22 in profit",
  "sensitivityNote": "Profit relatively stable around τ* (range: 75-82)",
  "policyRules": [
    {
      "scoreRange": "0 - 78",
      "action": "APPROVE",
      "description": "Low risk - Approve with standard policy"
    },
    {
      "scoreRange": "78 - 89",
      "action": "REQUIRE_PREPAY",
      "description": "Medium-High risk - Require prepayment"
    },
    {
      "scoreRange": "89 - 100",
      "action": "BLOCK_COD",
      "description": "Very High risk - Block COD, cash only"
    }
  ],
  "profitCurve": [
    { "threshold": 0, "profit": 45678.90, "ordersImpacted": 0, "revenueImpacted": 0 },
    { "threshold": 1, "profit": 45723.45, "ordersImpacted": 12, "revenueImpacted": 567.89 },
    ...
  ]
}
```

---

## 🎨 Dashboard Features

### **Interactive Controls**
1. **Threshold Slider (τ)**: 0-100 range, real-time updates
2. **Business Parameters**:
   - Return Processing Cost ($)
   - Shipping Cost ($)
   - COGS Ratio (%)
   - Conversion Rate Impact (%)

### **Visualizations**
1. **Profit Curve Chart**: Line chart showing Expected Profit vs τ
   - Highlights optimal threshold (τ*)
   - Shows profit plateau regions
   
2. **Impact Metrics Chart**: Bar chart displaying:
   - # Orders Impacted
   - Revenue at Risk

### **KPI Cards**
1. **Optimal Threshold (τ*)**: Recommended value
2. **Expected Profit Protection**: Monthly gain vs. baseline
3. **Orders Impacted %**: Percentage requiring policy action

### **Policy Decision Table**
Dynamic table showing:
- Risk score ranges
- Recommended actions (APPROVE, REQUIRE_PREPAY, BLOCK_COD)
- Action descriptions

### **Recommendation Box**
- Text recommendation summary
- Sensitivity analysis note

---

## 🚀 Usage Guide

### **For Inventory Managers**

1. **Access Dashboard**:
   - Navigate to: http://localhost:8080/sales/policy
   - Login with inventory manager credentials

2. **Find Optimal Policy**:
   - Click "Find Optimal τ*" button
   - System calculates best threshold automatically
   - Review KPI cards for impact summary

3. **What-If Analysis**:
   - Adjust threshold slider to test different policies
   - Modify business parameters (costs, conversion impact)
   - Click "Run Simulation" to see updated results

4. **Review Policy Table**:
   - Check recommended actions for each risk range
   - Understand gatekeeping rules

5. **Deploy Policy**:
   - Click "Deploy This Policy" to activate
   - Confirm deployment
   - Policy applies to future orders

### **Example Workflow**

**Scenario**: Reduce return losses while maintaining sales

1. Start with default parameters
2. Click "Find Optimal τ*"
3. System recommends τ* = 78 (protecting $2,211/month)
4. Adjust conversion impact from 20% to 15% (better retention)
5. Re-optimize: new τ* = 82 (protecting $2,456/month)
6. Deploy optimized policy

---

## 🔧 Configuration

### **Default Business Parameters**

```yaml
# application.yml
return-risk-policy:
  defaults:
    return-processing-cost: 15.0
    shipping-cost: 5.0
    cogs-ratio: 0.6           # 60%
    conversion-rate-impact: 0.2  # 20%
    optimal-threshold: 75.0
```

### **Model Weights (Python)**

```python
# pipelines/return_risk.py
alpha = 40.0  # Customer return rate weight
beta = 35.0   # SKU return rate weight
gamma = 15.0  # First-time customer weight
delta = 0.05  # Order value weight (inverse)
```

---

## 📊 Performance Metrics

### **KPIs to Monitor**

1. **Total Expected Profit(τ)**: Aggregate profit across all orders
2. **τ* Optimal Threshold**: Recommended value
3. **Orders Impacted %**: Percentage requiring gatekeeping
4. **Revenue at Risk**: Total revenue subject to policy
5. **Conversion Lift/Loss**: Actual impact on conversion rate
6. **False Positives**: Low-risk orders incorrectly blocked
7. **False Negatives**: High-risk orders incorrectly approved

### **Business Impact**

- **Baseline (τ=0, approve all)**: $45,678 profit
- **Optimal (τ*=78)**: $47,890 profit
- **Gain**: $2,212/month (+4.8%)

---

## 🛠️ Development & Testing

### **Run the Application**

1. **Start MongoDB**:
   ```bash
   docker run -d -p 27017:27017 mongo:latest
   ```

2. **Start Python Model Service**:
   ```bash
   cd model-service
   pip install -r requirements.txt
   python app.py
   # Runs on http://localhost:8000
   ```

3. **Start Spring Boot**:
   ```bash
   ./mvnw spring-boot:run
   # Runs on http://localhost:8080
   ```

4. **Access Dashboard**:
   ```
   http://localhost:8080/sales/policy
   ```

### **Test APIs**

```bash
# Test risk assessment
curl -X POST http://localhost:8080/api/policy/assess-risk \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD001","customerId":"CUST001","stockCode":"SKU123","quantity":1,"unitPrice":100}'

# Test simulation
curl -X POST http://localhost:8080/api/policy/simulate \
  -H "Content-Type: application/json" \
  -d '{"threshold":75,"returnProcessingCost":15,"conversionRateImpact":20,"cogsRatio":60,"shippingCostDefault":5}'

# Test optimization
curl -X POST http://localhost:8080/api/policy/optimize \
  -H "Content-Type: application/json" \
  -d '{"returnProcessingCost":15,"conversionRateImpact":20,"cogsRatio":60,"shippingCostDefault":5,"sampleSize":1000}'
```

---

## 🔮 Future Enhancements

1. **A/B Testing**: Compare policy variants in production
2. **ML Model Training**: Learn from actual return outcomes
3. **Multi-Country Policies**: Different τ* per country/channel
4. **Customer Segmentation Integration**: VIP customers get different thresholds
5. **Real-Time Monitoring**: Dashboard for deployed policy performance
6. **Automated Retraining**: Periodically update risk scores based on new data
7. **Explainability**: SHAP values for feature importance

---

## 📚 References

- **Deliverable A1**: Data Identification for Return-Risk Feature
- **Deliverable A2**: Model Component - Prescriptive Analytics
- **Deliverable A3**: UI Design - What-If Dashboard
- **Use Case Diagram**: `docs/usecase_inventory_manager.drawio`

---

## 👨‍💼 User Roles

- **Inventory Manager**: Primary user - manages policies, runs simulations
- **Operations Manager**: Monitors KPIs, reviews deployed policies
- **Data Scientist**: Tunes model weights, improves accuracy

---

## 🎓 Key Concepts

- **Prescriptive Analytics**: Recommends optimal actions, not just predictions
- **Expected Value**: Probability-weighted profit calculation
- **Threshold Optimization**: Finding τ* that maximizes objective function
- **What-If Analysis**: Interactive parameter exploration
- **Gatekeeping Policy**: Decision rules for order approval/rejection

---

## 📞 Support

For questions or issues:
- Check API documentation: http://localhost:8000/docs
- Review logs in console
- Contact: inventory-team@company.com

---

**Version**: 1.0.0  
**Last Updated**: October 27, 2025  
**Author**: DSS Team - G5
