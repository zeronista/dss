package com.g5.dss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @GetMapping("/cross-sell")
    public String crossSell(Model model) {
        // TODO: Add staff cross-sell data
        return "staff_cross_sell";
    }
}

