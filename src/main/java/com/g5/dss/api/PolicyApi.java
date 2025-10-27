package com.g5.dss.api;

import com.g5.dss.dto.*;
import com.g5.dss.service.ReturnRiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for Return-Risk Gatekeeping Policy - Prescriptive Decisions
 */
@RestController
@RequestMapping("/api/policy")
public class PolicyApi {
    
    private final ReturnRiskService returnRiskService;
    
    public PolicyApi(ReturnRiskService returnRiskService) {
        this.returnRiskService = returnRiskService;
    }
    
    /**
     * Assess return risk for a single order
     * 
     * POST /api/policy/assess-risk
     */
    @PostMapping("/assess-risk")
    public ResponseEntity<RiskAssessmentResponse> assessOrderRisk(
        @RequestBody OrderRiskRequest request,
        Authentication authentication
    ) {
        String username = authentication != null ? authentication.getName() : "system";
        RiskAssessmentResponse response = returnRiskService.assessOrderRisk(request, username);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Simulate policy with specific threshold
     * 
     * POST /api/policy/simulate
     */
    @PostMapping("/simulate")
    public ResponseEntity<PolicySimulationResponse> simulatePolicy(
        @RequestBody PolicySimulationRequest request
    ) {
        PolicySimulationResponse response = returnRiskService.simulatePolicy(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Find optimal threshold τ* that maximizes expected profit
     * 
     * POST /api/policy/optimize
     */
    @PostMapping("/optimize")
    public ResponseEntity<OptimalThresholdResponse> findOptimalThreshold(
        @RequestBody PolicySimulationRequest request
    ) {
        OptimalThresholdResponse response = returnRiskService.findOptimalThreshold(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Generate full profit curve for what-if visualization
     * 
     * POST /api/policy/profit-curve
     */
    @PostMapping("/profit-curve")
    public ResponseEntity<List<PolicySimulationResponse.ThresholdDataPoint>> getProfitCurve(
        @RequestBody PolicySimulationRequest request
    ) {
        List<PolicySimulationResponse.ThresholdDataPoint> curve = 
            returnRiskService.generateProfitCurve(request);
        return ResponseEntity.ok(curve);
    }
    
    /**
     * Get return risk prediction for specific order (legacy endpoint)
     * 
     * GET /api/policy/return-risk/{orderId}
     */
    @GetMapping("/return-risk/{orderId}")
    public ResponseEntity<RiskAssessmentResponse> getReturnRisk(@PathVariable String orderId) {
        // This would query from database in production
        // For now, return not implemented
        return ResponseEntity.notFound().build();
    }
}

