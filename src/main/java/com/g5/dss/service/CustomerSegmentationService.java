package com.g5.dss.service;

import com.g5.dss.dto.CustomerSegmentSummaryDTO;
import com.g5.dss.dto.RFMCustomerDTO;
import com.g5.dss.repository.jpa.OnlineRetailJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service phân tích RFM và phân khúc khách hàng
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerSegmentationService {
    
    private final OnlineRetailJpaRepository repository;
    
    /**
     * Tính toán RFM cho tất cả khách hàng
     */
    public List<RFMCustomerDTO> calculateRFM(LocalDateTime referenceDate) {
        log.info("Calculating RFM metrics with reference date: {}", referenceDate);
        
        // Group by customer and calculate RFM
        Map<Integer, List<com.g5.dss.domain.jpa.OnlineRetail>> customerGroups = 
            repository.findAll().stream()
                .filter(entity -> entity.getCustomerId() != null)
                .collect(Collectors.groupingBy(
                    com.g5.dss.domain.jpa.OnlineRetail::getCustomerId
                ));
        
        List<RFMCustomerDTO> result = customerGroups.entrySet().stream()
            .map(entry -> {
                Integer customerId = entry.getKey();
                List<com.g5.dss.domain.jpa.OnlineRetail> transactions = entry.getValue();
                
                // Tính Recency
                LocalDateTime lastPurchase = transactions.stream()
                    .map(com.g5.dss.domain.jpa.OnlineRetail::getInvoiceDate)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(referenceDate);
                long recency = ChronoUnit.DAYS.between(lastPurchase, referenceDate);
                
                // Tính Frequency (số đơn hàng unique)
                long frequency = transactions.stream()
                    .map(com.g5.dss.domain.jpa.OnlineRetail::getInvoiceNo)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();
                
                // Tính Monetary (tổng chi tiêu)
                double monetary = transactions.stream()
                    .filter(t -> t.getTotalAmount() != null)
                    .mapToDouble(t -> t.getTotalAmount().doubleValue())
                    .sum();
                
                // Country
                String country = transactions.get(0).getCountry();
                
                // Avg order value
                double avgOrderValue = frequency > 0 ? monetary / frequency : 0;
                
                // Total quantity
                int totalQuantity = transactions.stream()
                    .filter(t -> t.getQuantity() != null)
                    .mapToInt(com.g5.dss.domain.jpa.OnlineRetail::getQuantity)
                    .sum();
                
                return RFMCustomerDTO.builder()
                    .customerId(customerId)
                    .country(country)
                    .recency((int) recency)
                    .frequency((int) frequency)
                    .monetary(monetary)
                    .lastPurchaseDate(lastPurchase.toString())
                    .avgOrderValue(avgOrderValue)
                    .totalQuantity(totalQuantity)
                    .build();
            })
            .collect(Collectors.toList());
        
        log.info("Calculated RFM for {} customers", result.size());
        return result;
    }
    
    /**
     * Phân khúc khách hàng dựa trên RFM
     */
    public List<RFMCustomerDTO> segmentCustomers(List<RFMCustomerDTO> rfmData) {
        if (rfmData.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Tính quartiles cho R, F, M
        List<Integer> recencies = rfmData.stream()
            .map(RFMCustomerDTO::getRecency)
            .sorted()
            .collect(Collectors.toList());
        List<Integer> frequencies = rfmData.stream()
            .map(RFMCustomerDTO::getFrequency)
            .sorted()
            .collect(Collectors.toList());
        List<Double> monetaries = rfmData.stream()
            .map(RFMCustomerDTO::getMonetary)
            .sorted()
            .collect(Collectors.toList());
        
        double recencyQ25 = percentile(recencies, 25);
        double recencyQ50 = percentile(recencies, 50);
        double recencyQ75 = percentile(recencies, 75);
        
        double frequencyQ25 = percentile(frequencies, 25);
        double frequencyQ50 = percentile(frequencies, 50);
        double frequencyQ75 = percentile(frequencies, 75);
        
        double monetaryQ75 = percentile(monetaries, 75);
        
        // Gán segment cho từng khách hàng
        rfmData.forEach(customer -> {
            String segment = determineSegment(
                customer.getRecency(), customer.getFrequency(), customer.getMonetary(),
                recencyQ25, recencyQ50, recencyQ75,
                frequencyQ25, frequencyQ50, frequencyQ75,
                monetaryQ75
            );
            customer.setSegment(segment);
            customer.setSegmentId(getSegmentId(segment));
        });
        
        log.info("Segmented {} customers into 5 groups", rfmData.size());
        return rfmData;
    }
    
    /**
     * Tạo tổng hợp phân khúc
     */
    public List<CustomerSegmentSummaryDTO> getSegmentSummary(List<RFMCustomerDTO> segmentedData) {
        Map<String, List<RFMCustomerDTO>> grouped = segmentedData.stream()
            .collect(Collectors.groupingBy(RFMCustomerDTO::getSegment));
        
        int totalCustomers = segmentedData.size();
        
        return grouped.entrySet().stream()
            .map(entry -> {
                String segmentName = entry.getKey();
                List<RFMCustomerDTO> customers = entry.getValue();
                
                double totalValue = customers.stream()
                    .mapToDouble(RFMCustomerDTO::getMonetary)
                    .sum();
                
                double avgRecency = customers.stream()
                    .mapToDouble(RFMCustomerDTO::getRecency)
                    .average()
                    .orElse(0);
                
                double avgFrequency = customers.stream()
                    .mapToDouble(RFMCustomerDTO::getFrequency)
                    .average()
                    .orElse(0);
                
                double avgMonetary = customers.stream()
                    .mapToDouble(RFMCustomerDTO::getMonetary)
                    .average()
                    .orElse(0);
                
                return CustomerSegmentSummaryDTO.builder()
                    .segmentName(segmentName)
                    .customerCount(customers.size())
                    .totalValue(totalValue)
                    .avgRecency(avgRecency)
                    .avgFrequency(avgFrequency)
                    .avgMonetary(avgMonetary)
                    .percentageOfTotal((double) customers.size() / totalCustomers * 100)
                    .description(getSegmentDescription(segmentName, avgRecency, avgFrequency, avgMonetary))
                    .marketingActions(getMarketingActions(segmentName))
                    .build();
            })
            .sorted(Comparator.comparing(CustomerSegmentSummaryDTO::getTotalValue).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Lấy khách hàng có nguy cơ rời bỏ (At-Risk)
     */
    public List<RFMCustomerDTO> getAtRiskCustomers(List<RFMCustomerDTO> segmentedData) {
        return segmentedData.stream()
            .filter(c -> "At-Risk".equals(c.getSegment()) || "Hibernating".equals(c.getSegment()))
            .sorted(Comparator.comparing(RFMCustomerDTO::getMonetary).reversed())
            .collect(Collectors.toList());
    }
    
    // Helper methods
    
    private double percentile(List<? extends Number> sortedList, int percentile) {
        if (sortedList.isEmpty()) return 0;
        int index = (int) Math.ceil(percentile / 100.0 * sortedList.size()) - 1;
        index = Math.max(0, Math.min(index, sortedList.size() - 1));
        return sortedList.get(index).doubleValue();
    }
    
    private String determineSegment(
        int recency, int frequency, double monetary,
        double recencyQ25, double recencyQ50, double recencyQ75,
        double frequencyQ25, double frequencyQ50, double frequencyQ75,
        double monetaryQ75
    ) {
        // Champions: Low recency, high frequency, high monetary
        if (recency <= recencyQ25 && frequency >= frequencyQ75 && monetary >= monetaryQ75) {
            return "Champions";
        }
        
        // Loyal: Low-medium recency, medium-high frequency
        if (recency <= recencyQ50 && frequency >= frequencyQ50) {
            return "Loyal";
        }
        
        // At-Risk: High recency, low frequency
        if (recency >= recencyQ75 && frequency <= frequencyQ25) {
            return "At-Risk";
        }
        
        // Hibernating: High recency, low frequency
        if (recency >= recencyQ50 && frequency <= frequencyQ50) {
            return "Hibernating";
        }
        
        // Default: Regulars
        return "Regulars";
    }
    
    private int getSegmentId(String segment) {
        switch (segment) {
            case "Champions": return 0;
            case "Loyal": return 1;
            case "At-Risk": return 2;
            case "Hibernating": return 3;
            default: return 4; // Regulars
        }
    }
    
    private String getSegmentDescription(String segment, double avgRecency, double avgFrequency, double avgMonetary) {
        switch (segment) {
            case "Champions":
                return String.format("🏆 Nhóm khách hàng VIP nhất! Họ mua hàng thường xuyên (%.1f đơn/khách), " +
                    "chi tiêu cao (%.0f) và vừa mới quay lại (%.0f ngày trước).", 
                    avgFrequency, avgMonetary, avgRecency);
            
            case "Loyal":
                return String.format("💎 Khách hàng trung thành đáng tin cậy. Họ có tần suất mua hàng tốt (%.1f đơn) " +
                    "và chi tiêu ổn định (%.0f). Recency: %.0f ngày.", 
                    avgFrequency, avgMonetary, avgRecency);
            
            case "At-Risk":
                return String.format("⚠️ Nhóm có nguy cơ rời bỏ cao! Họ đã lâu không quay lại (%.0f ngày) " +
                    "và có tần suất mua thấp (%.1f đơn). CẦN HÀNH ĐỘNG NGAY!", 
                    avgRecency, avgFrequency);
            
            case "Hibernating":
                return String.format("😴 Khách hàng đang 'ngủ đông'. Họ đã rất lâu không quay lại (%.0f ngày) " +
                    "và có tần suất mua thấp (%.1f đơn).", 
                    avgRecency, avgFrequency);
            
            default: // Regulars
                return String.format("👥 Khách hàng thường xuyên ổn định. Recency: %.0f ngày, " +
                    "Frequency: %.1f đơn, Monetary: %.0f.", 
                    avgRecency, avgFrequency, avgMonetary);
        }
    }
    
    private List<String> getMarketingActions(String segment) {
        switch (segment) {
            case "Champions":
                return Arrays.asList(
                    "Ưu đãi VIP/early access",
                    "Chương trình giới thiệu bạn bè",
                    "Quà tặng đặc biệt"
                );
            
            case "Loyal":
                return Arrays.asList(
                    "Tích điểm, upsell gói sản phẩm",
                    "Ưu đãi sinh nhật",
                    "Cross-sell sản phẩm cao cấp"
                );
            
            case "At-Risk":
                return Arrays.asList(
                    "Email 'Chúng tôi nhớ bạn' + mã giảm 15%",
                    "Reactivation bundle giá tốt",
                    "Khảo sát feedback"
                );
            
            case "Hibernating":
                return Arrays.asList(
                    "Chiến dịch quay lại (remarketing)",
                    "Giảm phí vận chuyển",
                    "Flash sale độc quyền"
                );
            
            default: // Regulars
                return Arrays.asList(
                    "Khuyến mãi định kỳ",
                    "Cross-sell sản phẩm bổ trợ",
                    "Newsletter chất lượng"
                );
        }
    }
}
