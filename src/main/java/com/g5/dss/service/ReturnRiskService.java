package com.g5.dss.service;

import com.g5.dss.domain.mongo.PolicyConfiguration;
import com.g5.dss.domain.mongo.ReturnRiskScore;
import com.g5.dss.dto.*;
import com.g5.dss.repository.mongo.PolicyConfigurationRepository;
import com.g5.dss.repository.mongo.ReturnRiskScoreRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for Return Risk Assessment and Policy Management
 */
@Service
public class ReturnRiskService {
    
    private final ReturnRiskScoreRepository riskScoreRepository;
    private final PolicyConfigurationRepository policyRepository;
    private final RestTemplate restTemplate;
    
    @Value("${model-service.base-url:http://localhost:8000}")
    private String modelServiceUrl;
    
    public ReturnRiskService(
        ReturnRiskScoreRepository riskScoreRepository,
        PolicyConfigurationRepository policyRepository,
        RestTemplate restTemplate
    ) {
        this.riskScoreRepository = riskScoreRepository;
        this.policyRepository = policyRepository;
        this.restTemplate = restTemplate;
    }
    
    /**
     * Assess return risk for a single order
     */
    public RiskAssessmentResponse assessOrderRisk(OrderRiskRequest request, String username) {
        try {
            // Get active policy or use default
            PolicyConfiguration policy = getActivePolicy(request.getCountry());
            
            // Prepare request for ML service
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_id", request.getOrderId());
            orderData.put("customer_id", request.getCustomerId());
            orderData.put("stock_code", request.getStockCode());
            orderData.put("order_value", request.getQuantity() * request.getUnitPrice());
            
            // Add calculated features (would fetch from DB in production)
            orderData.put("customer_return_rate", calculateCustomerReturnRate(request.getCustomerId()));
            orderData.put("sku_return_rate", calculateSkuReturnRate(request.getStockCode()));
            orderData.put("is_first_time_customer", isFirstTimeCustomer(request.getCustomerId()));
            
            Map<String, Object> mlRequest = new HashMap<>();
            mlRequest.put("order_data", orderData);
            mlRequest.put("threshold", policy.getOptimalThreshold());
            mlRequest.put("params", buildPolicyParams(policy));
            
            // Call ML service
            String url = modelServiceUrl + "/policy/predict-risk";
            @SuppressWarnings("unchecked")
            Map<String, Object> mlResponse = restTemplate.postForObject(url, mlRequest, Map.class);
            
            // Save risk score to database
            ReturnRiskScore riskScore = saveRiskScore(mlResponse, request, policy, username);
            
            // Build response
            return buildRiskAssessmentResponse(mlResponse, policy);
            
        } catch (Exception e) {
            throw new RuntimeException("Error assessing order risk: " + e.getMessage(), e);
        }
    }
    
    /**
     * Simulate policy with specific threshold
     */
    public PolicySimulationResponse simulatePolicy(PolicySimulationRequest request) {
        try {
            // Fetch sample orders data
            List<Map<String, Object>> ordersData = fetchSampleOrders(request);
            
            Map<String, Object> mlRequest = new HashMap<>();
            mlRequest.put("orders_data", ordersData);
            mlRequest.put("threshold", request.getThreshold());
            mlRequest.put("params", buildSimulationParams(request));
            
            // Call ML service
            String url = modelServiceUrl + "/policy/simulate";
            @SuppressWarnings("unchecked")
            Map<String, Object> mlResponse = restTemplate.postForObject(url, mlRequest, Map.class);
            
            return buildSimulationResponse(mlResponse);
            
        } catch (Exception e) {
            throw new RuntimeException("Error simulating policy: " + e.getMessage(), e);
        }
    }
    
    /**
     * Find optimal threshold τ*
     */
    public OptimalThresholdResponse findOptimalThreshold(PolicySimulationRequest request) {
        try {
            // Fetch sample orders data
            List<Map<String, Object>> ordersData = fetchSampleOrders(request);
            
            Map<String, Object> mlRequest = new HashMap<>();
            mlRequest.put("orders_data", ordersData);
            mlRequest.put("params", buildSimulationParams(request));
            mlRequest.put("threshold_range", Arrays.asList(0, 100));
            mlRequest.put("step", 1.0);
            
            // Call ML service
            String url = modelServiceUrl + "/policy/optimize-threshold";
            @SuppressWarnings("unchecked")
            Map<String, Object> mlResponse = restTemplate.postForObject(url, mlRequest, Map.class);
            
            return buildOptimalThresholdResponse(mlResponse);
            
        } catch (Exception e) {
            throw new RuntimeException("Error finding optimal threshold: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate full profit curve for visualization
     */
    public List<PolicySimulationResponse.ThresholdDataPoint> generateProfitCurve(PolicySimulationRequest request) {
        OptimalThresholdResponse optimal = findOptimalThreshold(request);
        return optimal.getProfitCurve();
    }
    
    /**
     * Deploy a new policy configuration
     */
    public PolicyConfiguration deployPolicy(PolicyConfiguration policy, String username) {
        // Deactivate current active policies
        List<PolicyConfiguration> activePolicies = policyRepository.findByIsActiveTrue();
        activePolicies.forEach(p -> p.setIsActive(false));
        policyRepository.saveAll(activePolicies);
        
        // Activate new policy
        policy.setIsActive(true);
        policy.setActivatedAt(LocalDateTime.now());
        policy.setActivatedBy(username);
        policy.setLastModified(LocalDateTime.now());
        
        return policyRepository.save(policy);
    }
    
    /**
     * Get active policy for country (or global default)
     */
    private PolicyConfiguration getActivePolicy(String country) {
        // Try country-specific policy first
        if (country != null) {
            List<PolicyConfiguration> countryPolicies = policyRepository.findByCountry(country);
            Optional<PolicyConfiguration> activeCountryPolicy = countryPolicies.stream()
                .filter(PolicyConfiguration::getIsActive)
                .findFirst();
            if (activeCountryPolicy.isPresent()) {
                return activeCountryPolicy.get();
            }
        }
        
        // Fall back to global default
        return policyRepository.findByIsDefaultTrue()
            .orElseGet(this::createDefaultPolicy);
    }
    
    /**
     * Create default policy if none exists
     */
    private PolicyConfiguration createDefaultPolicy() {
        PolicyConfiguration policy = new PolicyConfiguration();
        policy.setPolicyName("Default Policy");
        policy.setDescription("System default return risk policy");
        policy.setOptimalThreshold(75.0);
        policy.setReturnProcessingCost(15.0);
        policy.setShippingCostDefault(5.0);
        policy.setCogsRatio(0.6);
        policy.setConversionRateImpact(0.2);
        policy.setIsActive(true);
        policy.setIsDefault(true);
        
        // Default thresholds
        List<PolicyConfiguration.PolicyThreshold> thresholds = new ArrayList<>();
        thresholds.add(new PolicyConfiguration.PolicyThreshold(0.0, 50.0, "APPROVE", "Low risk"));
        thresholds.add(new PolicyConfiguration.PolicyThreshold(50.0, 75.0, "REQUIRE_PREPAY", "Medium risk"));
        thresholds.add(new PolicyConfiguration.PolicyThreshold(75.0, 100.0, "BLOCK_COD", "High risk"));
        policy.setThresholds(thresholds);
        
        return policyRepository.save(policy);
    }
    
    /**
     * Build policy parameters for ML service
     */
    private Map<String, Double> buildPolicyParams(PolicyConfiguration policy) {
        Map<String, Double> params = new HashMap<>();
        params.put("return_processing_cost", policy.getReturnProcessingCost());
        params.put("shipping_cost", policy.getShippingCostDefault());
        params.put("cogs_ratio", policy.getCogsRatio());
        params.put("conversion_rate_impact", policy.getConversionRateImpact());
        return params;
    }
    
    /**
     * Build simulation parameters from request
     */
    private Map<String, Double> buildSimulationParams(PolicySimulationRequest request) {
        Map<String, Double> params = new HashMap<>();
        params.put("return_processing_cost", request.getReturnProcessingCost());
        params.put("shipping_cost", request.getShippingCostDefault());
        params.put("cogs_ratio", request.getCogsRatio() / 100.0);
        params.put("conversion_rate_impact", request.getConversionRateImpact() / 100.0);
        return params;
    }
    
    /**
     * Fetch sample orders for simulation (mock implementation)
     */
    private List<Map<String, Object>> fetchSampleOrders(PolicySimulationRequest request) {
        // In production, this would query actual order data from database
        // For now, return mock data
        List<Map<String, Object>> orders = new ArrayList<>();
        Random random = new Random(42); // Fixed seed for reproducibility
        
        int sampleSize = request.getSampleSize() != null ? request.getSampleSize() : 1000;
        
        for (int i = 0; i < sampleSize; i++) {
            Map<String, Object> order = new HashMap<>();
            order.put("order_id", "ORD" + i);
            order.put("customer_id", "CUST" + (i % 200));
            order.put("stock_code", "SKU" + (i % 50));
            order.put("order_value", 20 + random.nextDouble() * 200); // $20-$220
            order.put("customer_return_rate", random.nextDouble() * 0.3); // 0-30%
            order.put("sku_return_rate", random.nextDouble() * 0.25); // 0-25%
            order.put("is_first_time_customer", random.nextBoolean());
            orders.add(order);
        }
        
        return orders;
    }
    
    /**
     * Calculate customer return rate (mock - would query DB in production)
     */
    private Double calculateCustomerReturnRate(String customerId) {
        // In production: query historical orders for this customer
        return Math.random() * 0.3; // Mock: 0-30%
    }
    
    /**
     * Calculate SKU return rate (mock - would query DB in production)
     */
    private Double calculateSkuReturnRate(String stockCode) {
        // In production: query historical orders for this SKU
        return Math.random() * 0.25; // Mock: 0-25%
    }
    
    /**
     * Check if customer is first-time (mock - would query DB in production)
     */
    private Boolean isFirstTimeCustomer(String customerId) {
        // In production: check if customer has previous orders
        return Math.random() < 0.2; // Mock: 20% are first-time
    }
    
    /**
     * Save risk score to database
     */
    private ReturnRiskScore saveRiskScore(
        Map<String, Object> mlResponse,
        OrderRiskRequest request,
        PolicyConfiguration policy,
        String username
    ) {
        ReturnRiskScore score = new ReturnRiskScore();
        score.setOrderId(request.getOrderId());
        score.setCustomerId(request.getCustomerId());
        score.setStockCode(request.getStockCode());
        score.setRiskScore((Double) mlResponse.get("riskScore"));
        score.setRiskLevel((String) mlResponse.get("riskLevel"));
        score.setRecommendedAction((String) mlResponse.get("recommendedAction"));
        score.setActionReason((String) mlResponse.get("actionReason"));
        score.setExpectedProfitIfApproved((Double) mlResponse.get("expectedProfitIfApproved"));
        score.setExpectedProfitIfBlocked((Double) mlResponse.get("expectedProfitIfBlocked"));
        score.setRevenue(request.getQuantity() * request.getUnitPrice());
        score.setQuantity(request.getQuantity());
        score.setCountry(request.getCountry());
        score.setPolicyId(policy.getId());
        score.setThresholdUsed((Double) mlResponse.get("thresholdUsed"));
        score.setScoredBy(username);
        
        return riskScoreRepository.save(score);
    }
    
    /**
     * Build risk assessment response from ML response
     */
    private RiskAssessmentResponse buildRiskAssessmentResponse(
        Map<String, Object> mlResponse,
        PolicyConfiguration policy
    ) {
        RiskAssessmentResponse response = new RiskAssessmentResponse();
        response.setOrderId((String) mlResponse.get("orderId"));
        response.setRiskScore((Double) mlResponse.get("riskScore"));
        response.setRiskLevel((String) mlResponse.get("riskLevel"));
        response.setRecommendedAction((String) mlResponse.get("recommendedAction"));
        response.setActionReason((String) mlResponse.get("actionReason"));
        response.setExpectedProfitIfApproved((Double) mlResponse.get("expectedProfitIfApproved"));
        response.setExpectedProfitIfBlocked((Double) mlResponse.get("expectedProfitIfBlocked"));
        response.setProfitDifference((Double) mlResponse.get("profitDifference"));
        response.setFeatures((Map<String, Object>) mlResponse.get("features"));
        response.setPolicyId(policy.getId());
        response.setThresholdUsed((Double) mlResponse.get("thresholdUsed"));
        return response;
    }
    
    /**
     * Build simulation response from ML response
     */
    private PolicySimulationResponse buildSimulationResponse(Map<String, Object> mlResponse) {
        PolicySimulationResponse response = new PolicySimulationResponse();
        response.setThreshold((Double) mlResponse.get("threshold"));
        response.setTotalExpectedProfit((Double) mlResponse.get("totalExpectedProfit"));
        response.setTotalOrders((Integer) mlResponse.get("totalOrders"));
        response.setOrdersImpacted((Integer) mlResponse.get("ordersImpacted"));
        response.setOrdersImpactedPct((Double) mlResponse.get("ordersImpactedPct"));
        response.setRevenueAtRisk((Double) mlResponse.get("revenueAtRisk"));
        return response;
    }
    
    /**
     * Build optimal threshold response from ML response
     */
    @SuppressWarnings("unchecked")
    private OptimalThresholdResponse buildOptimalThresholdResponse(Map<String, Object> mlResponse) {
        OptimalThresholdResponse response = new OptimalThresholdResponse();
        response.setOptimalThreshold((Double) mlResponse.get("optimalThreshold"));
        response.setMaxExpectedProfit((Double) mlResponse.get("maxExpectedProfit"));
        response.setRecommendation((String) mlResponse.get("recommendation"));
        response.setProfitProtectedPerMonth((Double) mlResponse.get("profitGainVsBaseline"));
        response.setSensitivityNote((String) mlResponse.get("sensitivityNote"));
        
        // Convert policy rules
        List<Map<String, String>> rulesData = (List<Map<String, String>>) mlResponse.get("policyRules");
        List<OptimalThresholdResponse.PolicyRule> rules = rulesData.stream()
            .map(r -> new OptimalThresholdResponse.PolicyRule(
                r.get("scoreRange"),
                r.get("action"),
                r.get("description")
            ))
            .collect(Collectors.toList());
        response.setPolicyRules(rules);
        
        // Convert profit curve
        List<Map<String, Object>> curveData = (List<Map<String, Object>>) mlResponse.get("profitCurve");
        List<PolicySimulationResponse.ThresholdDataPoint> curve = curveData.stream()
            .map(d -> new PolicySimulationResponse.ThresholdDataPoint(
                (Double) d.get("threshold"),
                (Double) d.get("profit"),
                (Integer) d.get("ordersImpacted"),
                (Double) d.get("revenueImpacted")
            ))
            .collect(Collectors.toList());
        response.setProfitCurve(curve);
        
        return response;
    }
}
