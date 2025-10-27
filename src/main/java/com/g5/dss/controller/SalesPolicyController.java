package com.g5.dss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Sales Policy and Return-Risk Gatekeeping
 */
@Controller
@RequestMapping("/sales")
public class SalesPolicyController {

    @GetMapping("/policy")
    public String policy(Model model) {
        model.addAttribute("pageTitle", "Return-Risk Gatekeeping Policy");
        model.addAttribute("defaultThreshold", 75.0);
        model.addAttribute("defaultReturnCost", 15.0);
        model.addAttribute("defaultConversionImpact", 20.0);
        model.addAttribute("defaultCogsRatio", 60.0);
        model.addAttribute("defaultShippingCost", 5.0);
        return "sales_policy";
    }
}

