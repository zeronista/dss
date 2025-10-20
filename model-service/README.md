# DSS Model Service

Python FastAPI service for machine learning models and data pipelines.

## Features

- **RFM Analysis**: Customer segmentation based on Recency, Frequency, Monetary values
- **Association Rules**: Product recommendation and cross-selling using Apriori/FP-Growth
- **Return Risk Prediction**: ML-based prediction for sales return risk
- **Anomaly Detection**: Inventory anomaly detection for audit purposes

## Installation

```bash
# Create virtual environment
python -m venv venv

# Activate virtual environment
# Windows
venv\Scripts\activate
# Linux/Mac
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

## Running the Service

```bash
# Development mode with auto-reload
uvicorn app:app --reload --port 8000

# Production mode
python app.py
```

## API Documentation

Once running, visit:
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## Endpoints

### RFM Analysis
- `POST /rfm/analyze` - Perform RFM analysis
- `GET /rfm/segments` - Get cached segments

### Association Rules
- `POST /rules/generate` - Generate association rules
- `GET /rules/list` - Get cached rules

### Return Risk
- `POST /policy/predict-risk` - Predict return risk

### Anomaly Detection
- `POST /inventory/detect-anomalies` - Detect inventory anomalies

## Project Structure

```
model-service/
├── app.py                  # FastAPI application
├── pipelines/              # ML pipelines
│   ├── __init__.py
│   ├── rfm.py             # RFM analysis
│   ├── rules.py           # Association rules
│   ├── return_risk.py     # Return risk prediction
│   └── anomaly.py         # Anomaly detection
├── requirements.txt        # Python dependencies
└── README.md              # This file
```

## TODO

- [ ] Implement actual RFM calculation logic
- [ ] Implement Apriori/FP-Growth for association rules
- [ ] Train and integrate return risk ML model
- [ ] Implement Isolation Forest for anomaly detection
- [ ] Add database connection for data persistence
- [ ] Add authentication and authorization
- [ ] Add logging and monitoring
- [ ] Add unit tests

