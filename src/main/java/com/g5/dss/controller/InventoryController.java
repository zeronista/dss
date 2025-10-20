package com.g5.dss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    @GetMapping("/audit")
    public String audit(Model model) {
        // TODO: Add inventory audit data
        return "inventory_audit";
    }
}

