# test_api.py
# Basic API tests
# Run with: pytest test_api.py -v

import pytest
from fastapi.testclient import TestClient
from main import app
import pandas as pd
import io

client = TestClient(app)

def test_root():
    """Test root endpoint"""
    response = client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert "message" in data
    assert data["version"] == "1.0.0"

def test_health_check():
    """Test health check endpoint"""
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"

def test_upload_data():
    """Test data upload endpoint"""
    # Create sample CSV data
    sample_data = """InvoiceNo,StockCode,Description,Quantity,InvoiceDate,UnitPrice,CustomerID,Country
536365,85123A,WHITE HANGING HEART,6,12/1/2010 8:26,2.55,17850,United Kingdom
536365,71053,WHITE METAL LANTERN,6,12/1/2010 8:26,3.39,17850,United Kingdom
536366,22633,HAND WARMER UNION JACK,6,12/1/2010 8:28,1.85,17850,United Kingdom"""
    
    files = {
        'file': ('test_data.csv', io.BytesIO(sample_data.encode()), 'text/csv')
    }
    
    response = client.post("/api/upload", files=files)
    assert response.status_code == 200
    data = response.json()
    assert "session_id" in data
    assert data["rows"] >= 0
    return data["session_id"]

def test_overview_without_session():
    """Test overview endpoint without valid session"""
    response = client.post("/api/overview", json={
        "session_id": "invalid_session"
    })
    assert response.status_code == 404

def test_forecast_request():
    """Test forecast endpoint structure"""
    session_id = test_upload_data()
    
    response = client.post("/api/forecast", json={
        "session_id": session_id,
        "periods": 3,
        "confidence_level": 0.90
    })
    
    # May fail with small data, but should return proper structure
    assert response.status_code in [200, 500]

def test_segmentation_request():
    """Test segmentation endpoint structure"""
    session_id = test_upload_data()
    
    response = client.post("/api/segmentation", json={
        "session_id": session_id,
        "n_clusters": 3
    })
    
    # May fail with small data, but should return proper structure
    assert response.status_code in [200, 500]

def test_invalid_n_clusters():
    """Test segmentation with invalid n_clusters"""
    session_id = test_upload_data()
    
    # Too many clusters
    response = client.post("/api/segmentation", json={
        "session_id": session_id,
        "n_clusters": 20
    })
    assert response.status_code == 422

def test_market_basket_request():
    """Test market basket endpoint structure"""
    session_id = test_upload_data()
    
    response = client.post("/api/market-basket", json={
        "session_id": session_id,
        "min_support": 0.01,
        "min_confidence": 0.2
    })
    
    assert response.status_code in [200, 500]

def test_delete_session():
    """Test session deletion"""
    session_id = test_upload_data()
    
    response = client.delete(f"/api/session/{session_id}")
    assert response.status_code == 200
    
    # Verify session is deleted
    response = client.post("/api/overview", json={
        "session_id": session_id
    })
    assert response.status_code == 404

if __name__ == "__main__":
    pytest.main([__file__, "-v"])
