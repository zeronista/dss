package com.g5.dss.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/policy")
public class PolicyApi {

    @PostMapping("/simulate")
    public ResponseEntity<Map<String, Object>> simulatePolicy(@RequestBody Map<String, Object> params) {
        // TODO: Simulate sales policy with return risk prediction
        Map<String, Object> result = new HashMap<>();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/return-risk/{orderId}")
    public ResponseEntity<Map<String, Object>> getReturnRisk(@PathVariable String orderId) {
        // TODO: Get return risk prediction for order
        Map<String, Object> risk = new HashMap<>();
        return ResponseEntity.ok(risk);
    }
}

