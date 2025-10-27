package com.g5.dss.service;

import com.g5.dss.dto.MarketBasketRuleDTO;
import com.g5.dss.repository.jpa.OnlineRetailJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service phân tích Market Basket (Association Rules)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MarketBasketService {
    
    private final OnlineRetailJpaRepository repository;
    
    /**
     * Phát hiện quy tắc kết hợp sản phẩm
     * Sử dụng thuật toán đơn giản để tìm cặp sản phẩm thường được mua cùng nhau
     */
    public List<MarketBasketRuleDTO> findAssociationRules(
        Set<Integer> customerIds, 
        double minSupport, 
        double minConfidence,
        int maxRules
    ) {
        log.info("Finding association rules for {} customers with minSupport={}, minConfidence={}", 
            customerIds.size(), minSupport, minConfidence);
        
        // Lấy dữ liệu giao dịch
        Map<String, List<String>> invoiceItems = repository.findAll().stream()
            .filter(t -> customerIds == null || customerIds.contains(t.getCustomerId()))
            .filter(t -> t.getInvoiceNo() != null && t.getStockCode() != null)
            .collect(Collectors.groupingBy(
                t -> t.getInvoiceNo(),
                Collectors.mapping(
                    t -> t.getStockCode(),
                    Collectors.toList()
                )
            ));
        
        if (invoiceItems.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Map stock code to description
        Map<String, String> stockToDesc = repository.findAll().stream()
            .filter(t -> t.getStockCode() != null && t.getDescription() != null)
            .collect(Collectors.toMap(
                t -> t.getStockCode(),
                t -> t.getDescription(),
                (v1, v2) -> v1 // Keep first
            ));
        
        // Tìm các sản phẩm phổ biến (top 100)
        Map<String, Long> productFrequency = invoiceItems.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.groupingBy(
                stockCode -> stockCode,
                Collectors.counting()
            ));
        
        Set<String> topProducts = productFrequency.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(100)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        
        // Tính support, confidence, lift cho các cặp sản phẩm
        int totalInvoices = invoiceItems.size();
        List<MarketBasketRuleDTO> rules = new ArrayList<>();
        
        for (String productA : topProducts) {
            for (String productB : topProducts) {
                if (productA.equals(productB)) continue;
                
                // Đếm số invoice có A
                long countA = invoiceItems.values().stream()
                    .filter(items -> items.contains(productA))
                    .count();
                
                // Đếm số invoice có B
                long countB = invoiceItems.values().stream()
                    .filter(items -> items.contains(productB))
                    .count();
                
                // Đếm số invoice có cả A và B
                long countAB = invoiceItems.values().stream()
                    .filter(items -> items.contains(productA) && items.contains(productB))
                    .count();
                
                if (countAB == 0) continue;
                
                // Tính metrics
                double support = (double) countAB / totalInvoices;
                double confidence = (double) countAB / countA * 100; // % form
                double probB = (double) countB / totalInvoices;
                double lift = probB > 0 ? (support / probB) / ((double) countA / totalInvoices) : 0;
                
                // Lọc theo ngưỡng
                if (support >= minSupport && confidence >= minConfidence) {
                    String descA = stockToDesc.getOrDefault(productA, productA);
                    String descB = stockToDesc.getOrDefault(productB, productB);
                    
                    // Truncate descriptions
                    if (descA.length() > 50) descA = descA.substring(0, 47) + "...";
                    if (descB.length() > 50) descB = descB.substring(0, 47) + "...";
                    
                    rules.add(MarketBasketRuleDTO.builder()
                        .productACode(productA)
                        .productAName(descA)
                        .productBCode(productB)
                        .productBName(descB)
                        .support(support)
                        .confidence(confidence)
                        .lift(lift)
                        .transactionCount((int) countAB)
                        .recommendation(generateRecommendation(confidence, lift))
                        .build());
                }
            }
        }
        
        // Sort by confidence desc, then lift desc
        rules.sort(Comparator.comparing(MarketBasketRuleDTO::getConfidence).reversed()
            .thenComparing(Comparator.comparing(MarketBasketRuleDTO::getLift).reversed()));
        
        log.info("Found {} association rules", rules.size());
        
        return rules.stream()
            .limit(maxRules)
            .collect(Collectors.toList());
    }
    
    /**
     * Tạo gợi ý marketing dựa trên metrics
     */
    private String generateRecommendation(double confidence, double lift) {
        if (confidence > 70 && lift > 2.0) {
            return "🔥 Bundle mạnh - Tạo combo khuyến mãi";
        } else if (confidence > 60 && lift > 1.5) {
            return "✅ Bundle tốt - Hiển thị 'Mua kèm' trên web";
        } else if (confidence > 50) {
            return "📊 Bundle khả thi - Thử nghiệm trong email marketing";
        } else {
            return "⚠️ Bundle yếu - Cần xem xét thêm";
        }
    }
    
    /**
     * Lấy top N sản phẩm được mua cùng với 1 sản phẩm cho trước
     */
    public List<MarketBasketRuleDTO> getRecommendationsForProduct(
        String stockCode, 
        Set<Integer> customerIds,
        int topN
    ) {
        List<MarketBasketRuleDTO> allRules = findAssociationRules(customerIds, 0.01, 30, 100);
        
        return allRules.stream()
            .filter(rule -> rule.getProductACode().equals(stockCode))
            .limit(topN)
            .collect(Collectors.toList());
    }
}
