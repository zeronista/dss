package com.g5.dss.service;

import com.g5.dss.dto.PolicySimResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PolicyService {

    @SuppressWarnings("unused")
    private final RestTemplate restTemplate;
    @SuppressWarnings("unused")
    private static final String MODEL_SERVICE_URL = "http://localhost:8000";

    public PolicyService() {
        this.restTemplate = new RestTemplate();
    }

    public PolicySimResultDTO simulatePolicy(String orderId, Map<String, Object> params) {
        // TODO: Call model-service for return risk prediction
        PolicySimResultDTO result = new PolicySimResultDTO();
        result.setOrderId(orderId);
        return result;
    }

    public Double predictReturnRisk(String orderId) {
        // TODO: Get return risk score from model-service
        return 0.0;
    }
}

