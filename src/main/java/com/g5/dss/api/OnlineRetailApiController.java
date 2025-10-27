package com.g5.dss.api;

import com.g5.dss.dto.OnlineRetailDTO;
import com.g5.dss.dto.PagedResponse;
import com.g5.dss.service.OnlineRetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller cho Online Retail data từ MongoDB
 * 
 * Base URL: /api/retail
 */
@RestController
@RequestMapping("/api/retail")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Cho phép CORS nếu cần
public class OnlineRetailApiController {
    
    private final OnlineRetailService service;
    
    /**
     * GET /api/retail
     * Lấy tất cả dữ liệu với phân trang
     * 
     * Params:
     * - page: số trang (default: 0)
     * - size: số items/trang (default: 20)
     * - sortBy: field để sort (default: invoiceDate)
     */
    @GetMapping
    public ResponseEntity<PagedResponse<OnlineRetailDTO>> getAllData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "invoiceDate") String sortBy) {
        
        log.info("API: Get all retail data - page: {}, size: {}", page, size);
        PagedResponse<OnlineRetailDTO> response = service.getAllData(page, size, sortBy);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/retail/search
     * Tìm kiếm theo keyword
     * 
     * Params:
     * - keyword: từ khóa tìm kiếm
     * - page: số trang
     * - size: số items/trang
     */
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<OnlineRetailDTO>> searchData(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("API: Search retail data - keyword: {}", keyword);
        PagedResponse<OnlineRetailDTO> response = service.searchData(keyword, page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/retail/invoice/{invoiceNo}
     * Lấy chi tiết hóa đơn
     */
    @GetMapping("/invoice/{invoiceNo}")
    public ResponseEntity<List<OnlineRetailDTO>> getByInvoice(@PathVariable String invoiceNo) {
        log.info("API: Get retail data by invoice: {}", invoiceNo);
        List<OnlineRetailDTO> data = service.getByInvoiceNo(invoiceNo);
        return ResponseEntity.ok(data);
    }
    
    /**
     * GET /api/retail/customer/{customerId}
     * Lấy dữ liệu theo customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PagedResponse<OnlineRetailDTO>> getByCustomer(
            @PathVariable Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("API: Get retail data by customer: {}", customerId);
        PagedResponse<OnlineRetailDTO> response = service.getByCustomerId(customerId, page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/retail/country/{country}
     * Lấy dữ liệu theo quốc gia
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<PagedResponse<OnlineRetailDTO>> getByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("API: Get retail data by country: {}", country);
        PagedResponse<OnlineRetailDTO> response = service.getByCountry(country, page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/retail/product/{stockCode}
     * Lấy dữ liệu theo sản phẩm
     */
    @GetMapping("/product/{stockCode}")
    public ResponseEntity<List<OnlineRetailDTO>> getByProduct(@PathVariable String stockCode) {
        log.info("API: Get retail data by stock code: {}", stockCode);
        List<OnlineRetailDTO> data = service.getByStockCode(stockCode);
        return ResponseEntity.ok(data);
    }
    
    /**
     * GET /api/retail/stats
     * Lấy thống kê tổng quan
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("API: Get retail statistics");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", service.getTotalCount());
        stats.put("countries", service.getAllCountries());
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * GET /api/retail/countries
     * Lấy danh sách tất cả quốc gia
     */
    @GetMapping("/countries")
    public ResponseEntity<List<String>> getAllCountries() {
        log.info("API: Get all countries");
        List<String> countries = service.getAllCountries();
        return ResponseEntity.ok(countries);
    }
}
