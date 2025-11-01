# main.py
# Online Retail DSS API
# FastAPI implementation for data analysis, forecasting, segmentation and market basket
# Author: GitHub Copilot
# Run with: uvicorn main:app --reload

from fastapi import FastAPI, HTTPException, UploadFile, File, Query
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse, StreamingResponse
from typing import List, Optional, Dict, Any
from datetime import datetime, date
import pandas as pd
import numpy as np
import io
import warnings

from models import (
    DataUploadResponse,
    DateRangeFilter,
    OverviewMetrics,
    ForecastRequest,
    ForecastResponse,
    RFMRequest,
    RFMResponse,
    SegmentationRequest,
    SegmentationResponse,
    MarketBasketRequest,
    MarketBasketResponse,
    ChurnRequest,
    ChurnResponse
)

from services import (
    DataService,
    ForecastService,
    RFMService,
    SegmentationService,
    MarketBasketService,
    ChurnService
)

warnings.filterwarnings("ignore")

# Initialize FastAPI app
app = FastAPI(
    title="Online Retail DSS API",
    description="API for retail data analysis including forecasting, customer segmentation, and market basket analysis",
    version="1.0.0"
)

# CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Modify this in production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Global data store (in production, use database or cache)
data_store: Dict[str, pd.DataFrame] = {}

# Initialize services
data_service = DataService()
forecast_service = ForecastService()
rfm_service = RFMService()
segmentation_service = SegmentationService()
market_basket_service = MarketBasketService()
churn_service = ChurnService()

@app.get("/")
async def root():
    """Root endpoint with API information"""
    return {
        "message": "Online Retail DSS API",
        "version": "1.0.0",
        "endpoints": {
            "health": "/health",
            "upload": "/api/upload",
            "overview": "/api/overview",
            "forecast": "/api/forecast",
            "rfm": "/api/rfm",
            "segmentation": "/api/segmentation",
            "market_basket": "/api/market-basket",
            "churn": "/api/churn"
        }
    }

@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {
        "status": "healthy",
        "timestamp": datetime.now().isoformat(),
        "data_loaded": len(data_store) > 0
    }

@app.post("/api/upload", response_model=DataUploadResponse)
async def upload_data(file: UploadFile = File(...)):
    """
    Upload CSV file for analysis
    Expected columns: InvoiceNo, StockCode, Description, Quantity, InvoiceDate, UnitPrice, CustomerID, Country
    """
    try:
        # Read uploaded file
        contents = await file.read()
        df = pd.read_csv(io.BytesIO(contents), encoding="latin1")
        
        # Clean and process data
        df = data_service.clean_data(df)
        
        # Store in memory (use session ID in production)
        session_id = f"session_{datetime.now().timestamp()}"
        data_store[session_id] = df
        
        # Calculate basic stats
        stats = data_service.get_basic_stats(df)
        
        return DataUploadResponse(
            session_id=session_id,
            rows=len(df),
            columns=len(df.columns),
            date_range={
                "min": df['InvoiceDate'].min().isoformat(),
                "max": df['InvoiceDate'].max().isoformat()
            },
            stats=stats
        )
    
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Error processing file: {str(e)}")

@app.post("/api/overview", response_model=OverviewMetrics)
async def get_overview(filter_data: DateRangeFilter):
    """
    Get overview metrics and charts data
    """
    try:
        # Get data from store
        if filter_data.session_id not in data_store:
            raise HTTPException(status_code=404, detail="Session not found. Please upload data first.")
        
        df = data_store[filter_data.session_id]
        
        # Filter by date range
        if filter_data.start_date and filter_data.end_date:
            df = data_service.filter_by_date(df, filter_data.start_date, filter_data.end_date)
        
        # Calculate metrics
        metrics = data_service.calculate_overview_metrics(df)
        
        return metrics
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error calculating overview: {str(e)}")

@app.post("/api/forecast", response_model=ForecastResponse)
async def forecast_revenue(request: ForecastRequest):
    """
    Forecast future revenue using SARIMAX model
    """
    try:
        if request.session_id not in data_store:
            raise HTTPException(status_code=404, detail="Session not found")
        
        df = data_store[request.session_id]
        
        # Filter by date if provided
        if request.start_date and request.end_date:
            df = data_service.filter_by_date(df, request.start_date, request.end_date)
        
        # Generate forecast
        forecast_result = forecast_service.forecast_revenue(
            df, 
            periods=request.periods,
            confidence_level=request.confidence_level
        )
        
        return forecast_result
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating forecast: {str(e)}")

@app.post("/api/rfm", response_model=RFMResponse)
async def calculate_rfm(request: RFMRequest):
    """
    Calculate RFM (Recency, Frequency, Monetary) metrics for customers
    """
    try:
        if request.session_id not in data_store:
            raise HTTPException(status_code=404, detail="Session not found")
        
        df = data_store[request.session_id]
        
        # Filter by date if provided
        if request.start_date and request.end_date:
            df = data_service.filter_by_date(df, request.start_date, request.end_date)
        
        # Calculate RFM
        rfm_result = rfm_service.calculate_rfm(df, request.reference_date)
        
        return rfm_result
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error calculating RFM: {str(e)}")

@app.post("/api/segmentation", response_model=SegmentationResponse)
async def customer_segmentation(request: SegmentationRequest):
    """
    Perform customer segmentation using K-Means clustering on RFM data
    """
    try:
        if request.session_id not in data_store:
            raise HTTPException(status_code=404, detail="Session not found")
        
        df = data_store[request.session_id]
        
        # Filter by date if provided
        if request.start_date and request.end_date:
            df = data_service.filter_by_date(df, request.start_date, request.end_date)
        
        # Perform segmentation
        segmentation_result = segmentation_service.segment_customers(
            df,
            n_clusters=request.n_clusters,
            random_state=request.random_state
        )
        
        return segmentation_result
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error in segmentation: {str(e)}")

@app.post("/api/market-basket", response_model=MarketBasketResponse)
async def market_basket_analysis(request: MarketBasketRequest):
    """
    Perform market basket analysis to find product associations
    """
    try:
        if request.session_id not in data_store:
            raise HTTPException(status_code=404, detail="Session not found")
        
        df = data_store[request.session_id]
        
        # Filter by date if provided
        if request.start_date and request.end_date:
            df = data_service.filter_by_date(df, request.start_date, request.end_date)
        
        # Filter by segment if provided
        if request.customer_ids:
            df = df[df['CustomerID'].isin(request.customer_ids)]
        
        # Perform analysis
        basket_result = market_basket_service.analyze_basket(
            df,
            min_support=request.min_support,
            min_confidence=request.min_confidence,
            max_rules=request.max_rules
        )
        
        return basket_result
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error in market basket analysis: {str(e)}")

@app.post("/api/churn", response_model=ChurnResponse)
async def churn_prediction(request: ChurnRequest):
    """
    Identify customers at risk of churning based on RFM thresholds
    """
    try:
        if request.session_id not in data_store:
            raise HTTPException(status_code=404, detail="Session not found")
        
        df = data_store[request.session_id]
        
        # Filter by date if provided
        if request.start_date and request.end_date:
            df = data_service.filter_by_date(df, request.start_date, request.end_date)
        
        # Identify at-risk customers
        churn_result = churn_service.identify_churn_risk(
            df,
            recency_threshold_pct=request.recency_threshold_pct,
            frequency_threshold_pct=request.frequency_threshold_pct
        )
        
        return churn_result
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error in churn prediction: {str(e)}")

@app.get("/api/download/rfm/{session_id}")
async def download_rfm_csv(session_id: str):
    """
    Download RFM analysis results as CSV
    """
    try:
        if session_id not in data_store:
            raise HTTPException(status_code=404, detail="Session not found")
        
        df = data_store[session_id]
        rfm_result = rfm_service.calculate_rfm(df)
        
        # Convert to CSV
        output = io.StringIO()
        pd.DataFrame(rfm_result.customers).to_csv(output, index=False)
        output.seek(0)
        
        return StreamingResponse(
            io.BytesIO(output.getvalue().encode()),
            media_type="text/csv",
            headers={"Content-Disposition": f"attachment; filename=rfm_analysis_{session_id}.csv"}
        )
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating CSV: {str(e)}")

@app.get("/api/download/segments/{session_id}")
async def download_segments_csv(
    session_id: str, 
    n_clusters: int = Query(default=4, ge=2, le=10)
):
    """
    Download customer segmentation results as CSV
    """
    try:
        if session_id not in data_store:
            raise HTTPException(status_code=404, detail="Session not found")
        
        df = data_store[session_id]
        seg_result = segmentation_service.segment_customers(df, n_clusters=n_clusters)
        
        # Convert to CSV
        output = io.StringIO()
        pd.DataFrame(seg_result.segments).to_csv(output, index=False)
        output.seek(0)
        
        return StreamingResponse(
            io.BytesIO(output.getvalue().encode()),
            media_type="text/csv",
            headers={"Content-Disposition": f"attachment; filename=segments_{session_id}.csv"}
        )
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating CSV: {str(e)}")

@app.delete("/api/session/{session_id}")
async def delete_session(session_id: str):
    """
    Delete a data session to free up memory
    """
    if session_id in data_store:
        del data_store[session_id]
        return {"message": f"Session {session_id} deleted successfully"}
    else:
        raise HTTPException(status_code=404, detail="Session not found")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
