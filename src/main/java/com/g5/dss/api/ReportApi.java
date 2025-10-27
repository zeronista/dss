package com.g5.dss.api;

import com.g5.dss.service.OnlineRetailMySqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/report")
public class ReportApi {

    @Autowired
    private OnlineRetailMySqlService mySqlService;

    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = mySqlService.getOverallStats();
        
        // Calculate total revenue from country stats
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> countryStats = (List<Map<String, Object>>) stats.get("countryStats");
        BigDecimal totalRevenue = BigDecimal.ZERO;
        if (countryStats != null) {
            for (Map<String, Object> countryStat : countryStats) {
                Object revenue = countryStat.get("totalRevenue");
                if (revenue instanceof BigDecimal) {
                    totalRevenue = totalRevenue.add((BigDecimal) revenue);
                } else if (revenue instanceof Number) {
                    totalRevenue = totalRevenue.add(BigDecimal.valueOf(((Number) revenue).doubleValue()));
                }
            }
        }
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        
        @SuppressWarnings("unchecked")
        List<String> countries = (List<String>) stats.get("countries");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topCustomers = (List<Map<String, Object>>) stats.get("topCustomers");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topProducts = (List<Map<String, Object>>) stats.get("topProducts");
        
        response.put("totalCustomers", topCustomers != null ? topCustomers.size() * 100 : 0); // Estimate
        response.put("totalOrders", stats.get("totalRecords"));
        response.put("totalRevenue", totalRevenue);
        response.put("activeProducts", topProducts != null ? topProducts.size() * 100 : 0); // Estimate
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chart-data")
    public ResponseEntity<Map<String, Object>> getChartData(@RequestParam String type) {
        Map<String, Object> data = new HashMap<>();
        
        if ("sales".equals(type)) {
            // Top countries by sales
            List<Map<String, Object>> countryStats = mySqlService.getStatsByCountry();
            List<Map<String, Object>> top10 = countryStats.stream()
                .limit(10)
                .collect(Collectors.toList());
                
            data.put("labels", top10.stream()
                .map(c -> c.get("country"))
                .collect(Collectors.toList()));
            data.put("values", top10.stream()
                .map(c -> c.get("totalRevenue"))
                .collect(Collectors.toList()));
                
        } else if ("products".equals(type)) {
            // Top products
            List<Map<String, Object>> products = mySqlService.getTopProducts(10);
            
            data.put("labels", products.stream()
                .map(p -> {
                    String stockCode = (String) p.get("stockCode");
                    String desc = (String) p.get("description");
                    String label = stockCode;
                    if (desc != null && desc.length() > 0) {
                        label += " - " + (desc.length() > 20 ? desc.substring(0, 20) + "..." : desc);
                    }
                    return label;
                })
                .collect(Collectors.toList()));
            data.put("values", products.stream()
                .map(p -> p.get("totalQuantity"))
                .collect(Collectors.toList()));
        }
        
        return ResponseEntity.ok(data);
    }
}

