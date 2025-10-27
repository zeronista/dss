package com.g5.dss.controller;

import com.g5.dss.service.OnlineRetailMySqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private OnlineRetailMySqlService mySqlService;

    @GetMapping
    public String dashboard(Model model) {
        // Get overall statistics
        Map<String, Object> stats = mySqlService.getOverallStats();
        
        // Add dashboard data to model
        model.addAttribute("totalRecords", stats.get("totalRecords"));
        
        @SuppressWarnings("unchecked")
        List<String> countries = (List<String>) stats.get("countries");
        model.addAttribute("countryCount", countries != null ? countries.size() : 0);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topCustomers = (List<Map<String, Object>>) stats.get("topCustomers");
        model.addAttribute("uniqueCustomers", topCustomers != null ? topCustomers.size() : 0);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topProducts = (List<Map<String, Object>>) stats.get("topProducts");
        model.addAttribute("activeProducts", topProducts != null ? topProducts.size() : 0);
        
        return "dashboard";
    }
}

