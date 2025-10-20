package com.g5.dss.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffApi {

    @GetMapping("/recommendations/{customerId}")
    public ResponseEntity<List<Map<String, Object>>> getRecommendations(@PathVariable String customerId) {
        // TODO: Get cross-sell recommendations for customer
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformance() {
        // TODO: Get staff performance metrics
        return ResponseEntity.ok(new HashMap<>());
    }
}

