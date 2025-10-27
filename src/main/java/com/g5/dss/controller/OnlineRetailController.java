package com.g5.dss.controller;

import com.g5.dss.dto.PagedResponse;
import com.g5.dss.dto.OnlineRetailDTO;
import com.g5.dss.service.OnlineRetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Web Controller cho Online Retail data
 * 
 * URL: /retail
 */
@Controller
@RequestMapping("/retail")
@RequiredArgsConstructor
@Slf4j
public class OnlineRetailController {
    
    private final OnlineRetailService service;
    
    /**
     * GET /retail
     * Trang hiển thị dữ liệu retail
     */
    @GetMapping
    public String showRetailData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            Model model) {
        
        log.info("Showing retail data page - page: {}, size: {}, search: {}", page, size, search);
        
        PagedResponse<OnlineRetailDTO> data;
        
        // Nếu có search keyword
        if (search != null && !search.trim().isEmpty()) {
            data = service.searchData(search.trim(), page, size);
            model.addAttribute("search", search);
        } else {
            data = service.getAllData(page, size, "invoiceDate");
        }
        
        // Thống kê
        long totalRecords = service.getTotalCount();
        List<String> countries = service.getAllCountries();
        
        // Add to model
        model.addAttribute("data", data);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", data.getTotalPages());
        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("countries", countries);
        model.addAttribute("pageSize", size);
        
        return "retail_data"; // Template: templates/retail_data.html
    }
    
    /**
     * GET /retail/invoice
     * Trang chi tiết hóa đơn
     */
    @GetMapping("/invoice")
    public String showInvoiceDetail(
            @RequestParam String invoiceNo,
            Model model) {
        
        log.info("Showing invoice detail: {}", invoiceNo);
        
        List<OnlineRetailDTO> items = service.getByInvoiceNo(invoiceNo);
        
        // Tính tổng
        double total = items.stream()
                .mapToDouble(OnlineRetailDTO::getTotalAmount)
                .sum();
        
        model.addAttribute("invoiceNo", invoiceNo);
        model.addAttribute("items", items);
        model.addAttribute("totalAmount", total);
        model.addAttribute("itemCount", items.size());
        
        return "invoice_detail"; // Template: templates/invoice_detail.html
    }
}
