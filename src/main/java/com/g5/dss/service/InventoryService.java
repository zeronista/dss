package com.g5.dss.service;

import com.g5.dss.dto.AnomalyDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {

    @SuppressWarnings("unused")
    private final RestTemplate restTemplate;
    @SuppressWarnings("unused")
    private static final String MODEL_SERVICE_URL = "http://localhost:8000";

    public InventoryService() {
        this.restTemplate = new RestTemplate();
    }

    public List<AnomalyDTO> detectAnomalies() {
        // TODO: Call model-service for anomaly detection
        return List.of();
    }

    public Map<String, Object> getAuditReport() {
        // TODO: Generate inventory audit report
        return new HashMap<>();
    }
}

