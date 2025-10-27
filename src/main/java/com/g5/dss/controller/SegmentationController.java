package com.g5.dss.controller;

import com.g5.dss.dto.CustomerSegmentSummaryDTO;
import com.g5.dss.dto.RFMCustomerDTO;
import com.g5.dss.service.CustomerSegmentationService;
import com.g5.dss.service.MarketBasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller cho trang phân khúc khách hàng
 */
@Controller
@RequestMapping("/marketing")
@RequiredArgsConstructor
public class SegmentationController {
    
    private final CustomerSegmentationService segmentationService;
    private final MarketBasketService marketBasketService;
    
    /**
     * Trang tổng quan phân khúc
     */
    @GetMapping("/segments")
    public String segmentationOverview(Model model) {
        LocalDateTime referenceDate = LocalDateTime.now();
        
        // Calculate RFM and segments
        List<RFMCustomerDTO> rfmData = segmentationService.calculateRFM(referenceDate);
        List<RFMCustomerDTO> segmented = segmentationService.segmentCustomers(rfmData);
        List<CustomerSegmentSummaryDTO> summary = segmentationService.getSegmentSummary(segmented);
        
        model.addAttribute("totalCustomers", segmented.size());
        model.addAttribute("segments", summary);
        model.addAttribute("referenceDate", referenceDate);
        
        // At-risk count
        long atRiskCount = segmented.stream()
            .filter(c -> "At-Risk".equals(c.getSegment()) || "Hibernating".equals(c.getSegment()))
            .count();
        model.addAttribute("atRiskCount", atRiskCount);
        
        return "marketing_segments";
    }
    
    /**
     * Trang chi tiết phân khúc
     */
    @GetMapping("/segment/{segmentName}")
    public String segmentDetail(
        @PathVariable String segmentName,
        Model model
    ) {
        LocalDateTime referenceDate = LocalDateTime.now();
        
        // Get all customers in this segment
        List<RFMCustomerDTO> rfmData = segmentationService.calculateRFM(referenceDate);
        List<RFMCustomerDTO> segmented = segmentationService.segmentCustomers(rfmData);
        
        List<RFMCustomerDTO> segmentCustomers = segmented.stream()
            .filter(c -> segmentName.equalsIgnoreCase(c.getSegment()))
            .sorted((a, b) -> Double.compare(b.getMonetary(), a.getMonetary()))
            .collect(Collectors.toList());
        
        // Get segment summary
        List<CustomerSegmentSummaryDTO> allSummary = segmentationService.getSegmentSummary(segmented);
        CustomerSegmentSummaryDTO segmentSummary = allSummary.stream()
            .filter(s -> segmentName.equalsIgnoreCase(s.getSegmentName()))
            .findFirst()
            .orElse(null);
        
        model.addAttribute("segmentName", segmentName);
        model.addAttribute("customers", segmentCustomers);
        model.addAttribute("segmentSummary", segmentSummary);
        model.addAttribute("totalCustomers", segmented.size());
        
        return "segment_detail";
    }
    
    /**
     * Trang market basket analysis
     */
    @GetMapping("/market-basket")
    public String marketBasket(
        @RequestParam(required = false) String segment,
        Model model
    ) {
        model.addAttribute("selectedSegment", segment != null ? segment : "All");
        
        // Get available segments
        LocalDateTime referenceDate = LocalDateTime.now();
        List<RFMCustomerDTO> rfmData = segmentationService.calculateRFM(referenceDate);
        List<RFMCustomerDTO> segmented = segmentationService.segmentCustomers(rfmData);
        
        List<String> segments = segmented.stream()
            .map(RFMCustomerDTO::getSegment)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        model.addAttribute("segments", segments);
        
        return "market_basket";
    }
}
