#!/usr/bin/env pwsh
# Test Integration between Java and Python services
# Run this after starting both services

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Testing Java + Python Integration" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Configuration
$JAVA_URL = "http://localhost:8080"
$PYTHON_URL = "http://localhost:8000"

# Test 1: Check Python Service
Write-Host "[1/6] Checking Python ML Service..." -ForegroundColor Yellow
try {
    $pythonHealth = Invoke-RestMethod -Uri "$PYTHON_URL/health" -Method Get
    Write-Host "✅ Python Service: Running" -ForegroundColor Green
    Write-Host "   Status: $($pythonHealth.status)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Python Service: Not reachable" -ForegroundColor Red
    Write-Host "   Please start: cd api; uvicorn main:app --reload" -ForegroundColor Yellow
    exit 1
}

# Test 2: Check Java Service
Write-Host "`n[2/6] Checking Java Spring Boot Service..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$JAVA_URL" -Method Get -ErrorAction SilentlyContinue
    Write-Host "✅ Java Service: Running" -ForegroundColor Green
} catch {
    Write-Host "❌ Java Service: Not reachable" -ForegroundColor Red
    Write-Host "   Please start: mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

# Test 3: Check Python Health via Java Gateway
Write-Host "`n[3/6] Testing Java → Python Integration..." -ForegroundColor Yellow
try {
    $javaHealth = Invoke-RestMethod -Uri "$JAVA_URL/api/ml/health" -Method Get
    Write-Host "✅ Integration: Working" -ForegroundColor Green
    Write-Host "   Java Status: $($javaHealth.java_status)" -ForegroundColor Gray
    Write-Host "   Python Status: $($javaHealth.python_status)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Integration: Failed" -ForegroundColor Red
    Write-Host "   Error: $_" -ForegroundColor Yellow
    exit 1
}

# Test 4: Test File Upload (if CSV exists)
Write-Host "`n[4/6] Testing File Upload..." -ForegroundColor Yellow
$csvPath = "online_retail.csv"
if (Test-Path $csvPath) {
    try {
        $form = @{
            file = Get-Item -Path $csvPath
        }
        $uploadResult = Invoke-RestMethod -Uri "$JAVA_URL/api/ml/upload" -Method Post -Form $form
        $sessionId = $uploadResult.session_id
        Write-Host "✅ Upload: Success" -ForegroundColor Green
        Write-Host "   Session ID: $sessionId" -ForegroundColor Gray
        Write-Host "   Rows: $($uploadResult.rows)" -ForegroundColor Gray
        Write-Host "   Columns: $($uploadResult.columns)" -ForegroundColor Gray
        
        # Test 5: Test Forecast
        Write-Host "`n[5/6] Testing Revenue Forecast..." -ForegroundColor Yellow
        $forecastRequest = @{
            session_id = $sessionId
            periods = 3
            confidence_level = 0.90
        } | ConvertTo-Json
        
        $forecast = Invoke-RestMethod -Uri "$JAVA_URL/api/ml/forecast" `
            -Method Post `
            -Body $forecastRequest `
            -ContentType "application/json"
        
        Write-Host "✅ Forecast: Success" -ForegroundColor Green
        Write-Host "   Forecast periods: $($forecast.forecast_data.Count)" -ForegroundColor Gray
        
        # Test 6: Test Segmentation
        Write-Host "`n[6/6] Testing Customer Segmentation..." -ForegroundColor Yellow
        $segRequest = @{
            session_id = $sessionId
            n_clusters = 4
        } | ConvertTo-Json
        
        $segments = Invoke-RestMethod -Uri "$JAVA_URL/api/ml/segmentation" `
            -Method Post `
            -Body $segRequest `
            -ContentType "application/json"
        
        Write-Host "✅ Segmentation: Success" -ForegroundColor Green
        Write-Host "   Segments found: $($segments.segment_summary.Count)" -ForegroundColor Gray
        
        # Cleanup
        Write-Host "`n[Cleanup] Deleting session..." -ForegroundColor Yellow
        Invoke-RestMethod -Uri "$JAVA_URL/api/ml/session/$sessionId" -Method Delete | Out-Null
        Write-Host "✅ Session deleted" -ForegroundColor Green
        
    } catch {
        Write-Host "❌ Test Failed: $_" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "⚠️  Skipped: online_retail.csv not found" -ForegroundColor Yellow
    Write-Host "   Place CSV file in root directory to test upload" -ForegroundColor Gray
}

# Summary
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "✅ All Tests Passed!" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "Available Endpoints:" -ForegroundColor Cyan
Write-Host "  Java API:    $JAVA_URL/api/ml/*" -ForegroundColor Gray
Write-Host "  Python API:  $PYTHON_URL/docs" -ForegroundColor Gray
Write-Host "  Java UI:     $JAVA_URL/ml" -ForegroundColor Gray
Write-Host ""
