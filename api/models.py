# models.py
# Pydantic models for request/response validation
# Author: GitHub Copilot

from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from datetime import date, datetime

# ==================== Request Models ====================

class DateRangeFilter(BaseModel):
    """Filter data by date range"""
    session_id: str
    start_date: Optional[date] = None
    end_date: Optional[date] = None

class ForecastRequest(BaseModel):
    """Request for revenue forecasting"""
    session_id: str
    start_date: Optional[date] = None
    end_date: Optional[date] = None
    periods: int = Field(default=3, ge=1, le=12, description="Number of periods to forecast")
    confidence_level: float = Field(default=0.90, ge=0.5, le=0.99, description="Confidence interval level")

class RFMRequest(BaseModel):
    """Request for RFM analysis"""
    session_id: str
    start_date: Optional[date] = None
    end_date: Optional[date] = None
    reference_date: Optional[date] = None

class SegmentationRequest(BaseModel):
    """Request for customer segmentation"""
    session_id: str
    start_date: Optional[date] = None
    end_date: Optional[date] = None
    n_clusters: int = Field(default=4, ge=2, le=10, description="Number of segments")
    random_state: int = Field(default=42, description="Random state for reproducibility")

class MarketBasketRequest(BaseModel):
    """Request for market basket analysis"""
    session_id: str
    start_date: Optional[date] = None
    end_date: Optional[date] = None
    customer_ids: Optional[List[int]] = Field(default=None, description="Filter by specific customers")
    min_support: float = Field(default=0.01, ge=0.001, le=0.5, description="Minimum support threshold")
    min_confidence: float = Field(default=0.2, ge=0.1, le=1.0, description="Minimum confidence threshold")
    max_rules: int = Field(default=10, ge=1, le=100, description="Maximum number of rules to return")

class ChurnRequest(BaseModel):
    """Request for churn risk identification"""
    session_id: str
    start_date: Optional[date] = None
    end_date: Optional[date] = None
    recency_threshold_pct: float = Field(default=75, ge=50, le=95, description="Recency percentile threshold")
    frequency_threshold_pct: float = Field(default=25, ge=5, le=50, description="Frequency percentile threshold")

# ==================== Response Models ====================

class DataUploadResponse(BaseModel):
    """Response after uploading data"""
    session_id: str
    rows: int
    columns: int
    date_range: Dict[str, str]
    stats: Dict[str, Any]

class OverviewMetrics(BaseModel):
    """Overview metrics and KPIs"""
    total_revenue: float
    total_customers: int
    total_orders: int
    avg_order_value: float
    top_products: List[Dict[str, Any]]
    top_customers: List[Dict[str, Any]]
    monthly_revenue: List[Dict[str, Any]]
    revenue_by_country: List[Dict[str, Any]]
    insights: List[str]

class ForecastData(BaseModel):
    """Single forecast data point"""
    date: str
    predicted_value: float
    lower_bound: float
    upper_bound: float

class ForecastResponse(BaseModel):
    """Response for revenue forecasting"""
    historical_data: List[Dict[str, Any]]
    forecast_data: List[ForecastData]
    model_info: Dict[str, Any]
    insights: List[str]
    metrics: Dict[str, float]

class RFMCustomer(BaseModel):
    """RFM metrics for a single customer"""
    customer_id: int
    recency: int
    frequency: int
    monetary: float
    rfm_score: Optional[str] = None

class RFMResponse(BaseModel):
    """Response for RFM analysis"""
    customers: List[Dict[str, Any]]
    summary_stats: Dict[str, Any]
    distribution: Dict[str, List[int]]

class SegmentInfo(BaseModel):
    """Information about a customer segment"""
    segment_id: int
    segment_name: str
    customer_count: int
    total_value: float
    avg_recency: float
    avg_frequency: float
    avg_monetary: float
    characteristics: str
    marketing_actions: List[str]

class SegmentationResponse(BaseModel):
    """Response for customer segmentation"""
    segments: List[Dict[str, Any]]
    segment_summary: List[SegmentInfo]
    cluster_centers: List[List[float]]
    silhouette_score: Optional[float] = None

class AssociationRule(BaseModel):
    """Single association rule from market basket analysis"""
    antecedents: List[str]
    consequents: List[str]
    support: float
    confidence: float
    lift: float
    conviction: Optional[float] = None

class MarketBasketResponse(BaseModel):
    """Response for market basket analysis"""
    rules: List[Dict[str, Any]]
    top_rule: Optional[Dict[str, Any]] = None
    insights: List[str]
    metrics: Dict[str, Any]

class ChurnCustomer(BaseModel):
    """Customer at risk of churning"""
    customer_id: int
    recency: int
    frequency: int
    monetary: float
    churn_risk_score: float
    recommended_actions: List[str]

class ChurnResponse(BaseModel):
    """Response for churn risk analysis"""
    at_risk_customers: List[Dict[str, Any]]
    risk_summary: Dict[str, Any]
    recommendations: List[str]
    potential_value_at_risk: float
