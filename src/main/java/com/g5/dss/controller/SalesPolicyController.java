package com.g5.dss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sales")
public class SalesPolicyController {

    @GetMapping("/policy")
    public String policy(Model model) {
        // TODO: Add sales policy data
        return "sales_policy";
    }
}

