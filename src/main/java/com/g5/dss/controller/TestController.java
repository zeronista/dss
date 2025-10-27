package com.g5.dss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test-charts")
    public String testCharts() {
        return "test_charts";
    }
}
