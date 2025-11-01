package com.g5.dss.controller;

import com.g5.dss.service.PythonMLService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Web Controller for ML/AI Analytics Pages
 * 
 * This controller provides web pages that interact with Python ML service
 * for advanced analytics features.
 * 
 * Pages:
 * - /ml/forecast - Revenue forecasting
 * - /ml/segmentation - Customer segmentation
 * - /ml/market-basket - Product associations
 * - /ml/churn - Churn prediction
 * 
 * @author G5 Team
 */
@Controller
@RequestMapping("/ml")
@RequiredArgsConstructor
@Slf4j
public class MLAnalyticsController {
    
    private final PythonMLService pythonMLService;
    
    /**
     * ML Analytics Dashboard
     * GET /ml
     */
    @GetMapping
    public String mlDashboard(Model model) {
        log.info("Accessing ML Analytics Dashboard");
        
        // Check Python service health
        Map<String, Object> health = pythonMLService.checkHealth();
        model.addAttribute("pythonHealth", health);
        
        return "ml_dashboard";
    }
    
    /**
     * Revenue Forecasting Page
     * GET /ml/forecast
     */
    @GetMapping("/forecast")
    public String forecastPage(Model model) {
        log.info("Accessing Revenue Forecast page");
        
        Map<String, Object> health = pythonMLService.checkHealth();
        model.addAttribute("pythonHealth", health);
        model.addAttribute("pageTitle", "Dự báo Doanh thu");
        
        return "ml_forecast";
    }
    
    /**
     * Customer Segmentation Page
     * GET /ml/segmentation
     */
    @GetMapping("/segmentation")
    public String segmentationPage(Model model) {
        log.info("Accessing Customer Segmentation page");
        
        Map<String, Object> health = pythonMLService.checkHealth();
        model.addAttribute("pythonHealth", health);
        model.addAttribute("pageTitle", "Phân khúc Khách hàng");
        
        return "ml_segmentation";
    }
    
    /**
     * Market Basket Analysis Page
     * GET /ml/market-basket
     */
    @GetMapping("/market-basket")
    public String marketBasketPage(Model model) {
        log.info("Accessing Market Basket Analysis page");
        
        Map<String, Object> health = pythonMLService.checkHealth();
        model.addAttribute("pythonHealth", health);
        model.addAttribute("pageTitle", "Phân tích Giỏ hàng");
        
        return "ml_market_basket";
    }
    
    /**
     * Churn Prediction Page
     * GET /ml/churn
     */
    @GetMapping("/churn")
    public String churnPage(Model model) {
        log.info("Accessing Churn Prediction page");
        
        Map<String, Object> health = pythonMLService.checkHealth();
        model.addAttribute("pythonHealth", health);
        model.addAttribute("pageTitle", "Dự đoán Rời bỏ");
        
        return "ml_churn";
    }
    
    /**
     * RFM Analysis Page
     * GET /ml/rfm
     */
    @GetMapping("/rfm")
    public String rfmPage(Model model) {
        log.info("Accessing RFM Analysis page");
        
        Map<String, Object> health = pythonMLService.checkHealth();
        model.addAttribute("pythonHealth", health);
        model.addAttribute("pageTitle", "Phân tích RFM");
        
        return "ml_rfm";
    }
}
