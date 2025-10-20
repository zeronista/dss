from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Dict, Any
import uvicorn

from pipelines.rfm import RFMPipeline
from pipelines.rules import AssociationRulesPipeline
from pipelines.return_risk import ReturnRiskPipeline
from pipelines.anomaly import AnomalyDetectionPipeline

app = FastAPI(title="DSS Model Service", version="1.0.0")

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize pipelines
rfm_pipeline = RFMPipeline()
rules_pipeline = AssociationRulesPipeline()
risk_pipeline = ReturnRiskPipeline()
anomaly_pipeline = AnomalyDetectionPipeline()


# Request/Response Models
class RFMRequest(BaseModel):
    customer_data: List[Dict[str, Any]]


class RulesRequest(BaseModel):
    transaction_data: List[Dict[str, Any]]
    min_support: float = 0.01
    min_confidence: float = 0.5


class ReturnRiskRequest(BaseModel):
    order_data: Dict[str, Any]


class AnomalyRequest(BaseModel):
    inventory_data: List[Dict[str, Any]]


# Health check
@app.get("/")
def root():
    return {"status": "ok", "service": "DSS Model Service"}


# RFM Analysis
@app.post("/rfm/analyze")
def analyze_rfm(request: RFMRequest):
    """Perform RFM analysis and customer segmentation"""
    try:
        segments = rfm_pipeline.analyze(request.customer_data)
        return {"segments": segments}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/rfm/segments")
def get_rfm_segments():
    """Get cached RFM segments"""
    try:
        segments = rfm_pipeline.get_cached_segments()
        return {"segments": segments}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Association Rules
@app.post("/rules/generate")
def generate_rules(request: RulesRequest):
    """Generate association rules from transaction data"""
    try:
        rules = rules_pipeline.generate(
            request.transaction_data,
            min_support=request.min_support,
            min_confidence=request.min_confidence
        )
        return {"rules": rules}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/rules/list")
def list_rules():
    """Get cached association rules"""
    try:
        rules = rules_pipeline.get_cached_rules()
        return {"rules": rules}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Return Risk Prediction
@app.post("/policy/predict-risk")
def predict_return_risk(request: ReturnRiskRequest):
    """Predict return risk for an order"""
    try:
        prediction = risk_pipeline.predict(request.order_data)
        return prediction
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Anomaly Detection
@app.post("/inventory/detect-anomalies")
def detect_anomalies(request: AnomalyRequest):
    """Detect anomalies in inventory data"""
    try:
        anomalies = anomaly_pipeline.detect(request.inventory_data)
        return {"anomalies": anomalies}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000, reload=True)

