package com.g5.dss.api;

import com.g5.dss.dto.CustomerSegmentSummaryDTO;
import com.g5.dss.dto.MarketBasketRuleDTO;
import com.g5.dss.dto.RFMCustomerDTO;
import com.g5.dss.service.CustomerSegmentationService;
import com.g5.dss.service.MarketBasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * API Controller cho phân khúc khách hàng và market basket analysis
 */
@RestController
@RequestMapping("/api/segmentation")
@RequiredArgsConstructor
public class SegmentationApiController {
    
    private final CustomerSegmentationService segmentationService;
    private final MarketBasketService marketBasketService;
    
    /**
     * GET /api/segmentation/rfm
     * Tính toán RFM cho tất cả khách hàng
     */
    @GetMapping("/rfm")
    public ResponseEntity<List<RFMCustomerDTO>> calculateRFM() {
        LocalDateTime referenceDate = LocalDateTime.now();
        List<RFMCustomerDTO> rfmData = segmentationService.calculateRFM(referenceDate);
        List<RFMCustomerDTO> segmented = segmentationService.segmentCustomers(rfmData);
        return ResponseEntity.ok(segmented);
    }
    
    /**
     * GET /api/segmentation/summary
     * Lấy tổng quan các phân khúc
     */
    @GetMapping("/summary")
    public ResponseEntity<List<CustomerSegmentSummaryDTO>> getSegmentSummary() {
        LocalDateTime referenceDate = LocalDateTime.now();
        List<RFMCustomerDTO> rfmData = segmentationService.calculateRFM(referenceDate);
        List<RFMCustomerDTO> segmented = segmentationService.segmentCustomers(rfmData);
        List<CustomerSegmentSummaryDTO> summary = segmentationService.getSegmentSummary(segmented);
        return ResponseEntity.ok(summary);
    }
    
    /**
     * GET /api/segmentation/at-risk
     * Lấy danh sách khách hàng có nguy cơ rời bỏ
     */
    @GetMapping("/at-risk")
    public ResponseEntity<Map<String, Object>> getAtRiskCustomers() {
        LocalDateTime referenceDate = LocalDateTime.now();
        List<RFMCustomerDTO> rfmData = segmentationService.calculateRFM(referenceDate);
        List<RFMCustomerDTO> segmented = segmentationService.segmentCustomers(rfmData);
        List<RFMCustomerDTO> atRisk = segmentationService.getAtRiskCustomers(segmented);
        
        Map<String, Object> result = new HashMap<>();
        result.put("atRiskCustomers", atRisk);
        result.put("totalCustomers", segmented.size());
        result.put("atRiskCount", atRisk.size());
        result.put("atRiskPercentage", segmented.size() > 0 ? 
            (double) atRisk.size() / segmented.size() * 100 : 0);
        
        // Calculate potential value
        double potentialValue = atRisk.stream()
            .mapToDouble(RFMCustomerDTO::getMonetary)
            .sum();
        result.put("potentialValue", potentialValue);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * GET /api/segmentation/segment/{segmentName}
     * Lấy danh sách khách hàng trong một phân khúc
     */
    @GetMapping("/segment/{segmentName}")
    public ResponseEntity<List<RFMCustomerDTO>> getCustomersBySegment(
        @PathVariable String segmentName
    ) {
        LocalDateTime referenceDate = LocalDateTime.now();
        List<RFMCustomerDTO> rfmData = segmentationService.calculateRFM(referenceDate);
        List<RFMCustomerDTO> segmented = segmentationService.segmentCustomers(rfmData);
        
        List<RFMCustomerDTO> filtered = segmented.stream()
            .filter(c -> segmentName.equalsIgnoreCase(c.getSegment()))
            .sorted(Comparator.comparing(RFMCustomerDTO::getMonetary).reversed())
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(filtered);
    }
    
    /**
     * POST /api/segmentation/market-basket
     * Phân tích market basket cho một phân khúc
     */
    @PostMapping("/market-basket")
    public ResponseEntity<List<MarketBasketRuleDTO>> analyzeMarketBasket(
        @RequestParam(required = false) String segment,
        @RequestParam(defaultValue = "0.01") double minSupport,
        @RequestParam(defaultValue = "30") double minConfidence,
        @RequestParam(defaultValue = "10") int maxRules
    ) {
        // Get customer IDs for the segment
        Set<Integer> customerIds = null;
        
        if (segment != null && !segment.isEmpty()) {
            LocalDateTime referenceDate = LocalDateTime.now();
            List<RFMCustomerDTO> rfmData = segmentationService.calculateRFM(referenceDate);
            List<RFMCustomerDTO> segmented = segmentationService.segmentCustomers(rfmData);
            
            customerIds = segmented.stream()
                .filter(c -> segment.equalsIgnoreCase(c.getSegment()))
                .map(RFMCustomerDTO::getCustomerId)
                .collect(Collectors.toSet());
        }
        
        List<MarketBasketRuleDTO> rules = marketBasketService.findAssociationRules(
            customerIds, minSupport, minConfidence, maxRules
        );
        
        return ResponseEntity.ok(rules);
    }
    
    /**
     * GET /api/segmentation/product-recommendations/{stockCode}
     * Lấy gợi ý sản phẩm cho 1 sản phẩm
     */
    @GetMapping("/product-recommendations/{stockCode}")
    public ResponseEntity<List<MarketBasketRuleDTO>> getProductRecommendations(
        @PathVariable String stockCode,
        @RequestParam(required = false) String segment,
        @RequestParam(defaultValue = "5") int topN
    ) {
        // Get customer IDs for the segment
        Set<Integer> customerIds = null;
        
        if (segment != null && !segment.isEmpty()) {
            LocalDateTime referenceDate = LocalDateTime.now();
            List<RFMCustomerDTO> rfmData = segmentationService.calculateRFM(referenceDate);
            List<RFMCustomerDTO> segmented = segmentationService.segmentCustomers(rfmData);
            
            customerIds = segmented.stream()
                .filter(c -> segment.equalsIgnoreCase(c.getSegment()))
                .map(RFMCustomerDTO::getCustomerId)
                .collect(Collectors.toSet());
        }
        
        List<MarketBasketRuleDTO> recommendations = marketBasketService.getRecommendationsForProduct(
            stockCode, customerIds, topN
        );
        
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * GET /api/segmentation/stats
     * Lấy thống kê tổng quan
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSegmentationStats() {
        LocalDateTime referenceDate = LocalDateTime.now();
        List<RFMCustomerDTO> rfmData = segmentationService.calculateRFM(referenceDate);
        List<RFMCustomerDTO> segmented = segmentationService.segmentCustomers(rfmData);
        List<CustomerSegmentSummaryDTO> summary = segmentationService.getSegmentSummary(segmented);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", segmented.size());
        stats.put("segments", summary);
        
        // Champion stats
        long championCount = segmented.stream()
            .filter(c -> "Champions".equals(c.getSegment()))
            .count();
        stats.put("championCount", championCount);
        stats.put("championPercentage", segmented.size() > 0 ? 
            (double) championCount / segmented.size() * 100 : 0);
        
        // At-risk stats
        long atRiskCount = segmented.stream()
            .filter(c -> "At-Risk".equals(c.getSegment()) || "Hibernating".equals(c.getSegment()))
            .count();
        stats.put("atRiskCount", atRiskCount);
        stats.put("atRiskPercentage", segmented.size() > 0 ? 
            (double) atRiskCount / segmented.size() * 100 : 0);
        
        return ResponseEntity.ok(stats);
    }
}
