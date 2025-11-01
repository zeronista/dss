"""
Example usage of the Online Retail DSS API
Run this after starting the API server
"""

import requests
import json

# API base URL
BASE_URL = "http://localhost:8000"

def test_health():
    """Test health check"""
    print("=== Testing Health Check ===")
    response = requests.get(f"{BASE_URL}/health")
    print(f"Status: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}\n")

def upload_data(file_path):
    """Upload CSV file"""
    print("=== Uploading Data ===")
    with open(file_path, 'rb') as f:
        files = {'file': f}
        response = requests.post(f"{BASE_URL}/api/upload", files=files)
    
    print(f"Status: {response.status_code}")
    data = response.json()
    print(f"Session ID: {data['session_id']}")
    print(f"Rows: {data['rows']}")
    print(f"Columns: {data['columns']}\n")
    return data['session_id']

def get_overview(session_id, start_date=None, end_date=None):
    """Get overview metrics"""
    print("=== Getting Overview ===")
    payload = {"session_id": session_id}
    if start_date:
        payload["start_date"] = start_date
    if end_date:
        payload["end_date"] = end_date
    
    response = requests.post(f"{BASE_URL}/api/overview", json=payload)
    print(f"Status: {response.status_code}")
    data = response.json()
    print(f"Total Revenue: {data['total_revenue']:,.2f}")
    print(f"Total Customers: {data['total_customers']:,}")
    print(f"Total Orders: {data['total_orders']:,}")
    print(f"\nTop Insights:")
    for insight in data['insights'][:3]:
        print(f"  - {insight}")
    print()

def forecast_revenue(session_id, periods=3):
    """Forecast future revenue"""
    print(f"=== Forecasting {periods} Periods ===")
    payload = {
        "session_id": session_id,
        "periods": periods,
        "confidence_level": 0.90
    }
    
    response = requests.post(f"{BASE_URL}/api/forecast", json=payload)
    print(f"Status: {response.status_code}")
    data = response.json()
    
    print("Forecast Results:")
    for fc in data['forecast_data']:
        print(f"  {fc['date']}: {fc['predicted_value']:,.2f} "
              f"[{fc['lower_bound']:,.2f} - {fc['upper_bound']:,.2f}]")
    
    print(f"\nInsights:")
    for insight in data['insights']:
        print(f"  - {insight}")
    print()

def segment_customers(session_id, n_clusters=4):
    """Perform customer segmentation"""
    print(f"=== Customer Segmentation ({n_clusters} clusters) ===")
    payload = {
        "session_id": session_id,
        "n_clusters": n_clusters,
        "random_state": 42
    }
    
    response = requests.post(f"{BASE_URL}/api/segmentation", json=payload)
    print(f"Status: {response.status_code}")
    data = response.json()
    
    print(f"Silhouette Score: {data.get('silhouette_score', 'N/A')}")
    print("\nSegment Summary:")
    for seg in data['segment_summary']:
        print(f"\n  {seg['segment_name']}:")
        print(f"    Customers: {seg['customer_count']:,}")
        print(f"    Total Value: {seg['total_value']:,.2f}")
        print(f"    Avg Recency: {seg['avg_recency']:.1f} days")
        print(f"    Marketing Actions:")
        for action in seg['marketing_actions'][:2]:
            print(f"      - {action}")
    print()

def market_basket(session_id, min_support=0.01):
    """Analyze market basket"""
    print("=== Market Basket Analysis ===")
    payload = {
        "session_id": session_id,
        "min_support": min_support,
        "min_confidence": 0.3,
        "max_rules": 5
    }
    
    response = requests.post(f"{BASE_URL}/api/market-basket", json=payload)
    print(f"Status: {response.status_code}")
    data = response.json()
    
    if data['top_rule']:
        top = data['top_rule']
        print(f"\nTop Rule:")
        print(f"  {' + '.join(top['antecedents'][:2])}")
        print(f"  -> {' + '.join(top['consequents'][:2])}")
        print(f"  Confidence: {top['confidence']*100:.1f}%")
        print(f"  Lift: {top['lift']:.2f}")
    
    print(f"\nTotal Rules Found: {data['metrics']['total_rules']}")
    print()

def identify_churn(session_id):
    """Identify at-risk customers"""
    print("=== Churn Risk Analysis ===")
    payload = {
        "session_id": session_id,
        "recency_threshold_pct": 75,
        "frequency_threshold_pct": 25
    }
    
    response = requests.post(f"{BASE_URL}/api/churn", json=payload)
    print(f"Status: {response.status_code}")
    data = response.json()
    
    summary = data['risk_summary']
    print(f"Risk Level: {summary['risk_level']}")
    print(f"At-Risk Customers: {summary['at_risk_count']:,} / {summary['total_customers']:,}")
    print(f"Risk Percentage: {summary['risk_percentage']:.1f}%")
    print(f"Potential Value at Risk: {data['potential_value_at_risk']:,.2f}")
    
    print(f"\nRecommendations:")
    for rec in data['recommendations'][:3]:
        print(f"  - {rec}")
    print()

def main():
    """Run example workflow"""
    print("\n" + "="*60)
    print("Online Retail DSS API - Example Usage")
    print("="*60 + "\n")
    
    # Test health
    test_health()
    
    # Upload data (replace with your CSV path)
    csv_path = "online_retail.csv"
    
    try:
        session_id = upload_data(csv_path)
        
        # Run analysis
        get_overview(session_id, "2010-12-01", "2011-12-09")
        forecast_revenue(session_id, periods=3)
        segment_customers(session_id, n_clusters=4)
        market_basket(session_id, min_support=0.01)
        identify_churn(session_id)
        
        print("="*60)
        print("Example completed successfully!")
        print("="*60)
        
    except FileNotFoundError:
        print(f"Error: Could not find '{csv_path}'")
        print("Please place the online_retail.csv file in the same directory")
    except requests.exceptions.ConnectionError:
        print("Error: Could not connect to API server")
        print("Please make sure the API is running: uvicorn main:app --reload")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()
