package com.g5.dss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/marketing")
public class MarketingController {

    @GetMapping("/segments")
    public String segments(Model model) {
        // TODO: Add RFM segments data
        return "marketing_segments";
    }

    @GetMapping("/rules")
    public String rules(Model model) {
        // TODO: Add marketing rules data
        return "marketing_rules";
    }
}

