package com.g5.dss.api;

import com.g5.dss.dto.OnlineRetailDTO;
import com.g5.dss.dto.PagedResponse;
import com.g5.dss.service.OnlineRetailMySqlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller cho Online Retail data từ MySQL
 * 
 * Base URL: /api/mysql/retail
 */
@RestController
@RequestMapping("/api/mysql/retail")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OnlineRetailMySqlApiController {
    
    private final OnlineRetailMySqlService service;
    
    /**
     * GET /api/mysql/retail
     * Lấy tất cả dữ liệu với phân trang
     */
    @GetMapping
    public ResponseEntity<PagedResponse<OnlineRetailDTO>> getAllData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "invoiceDate") String sortBy) {
        
        log.info("API MySQL: Get all retail data - page: {}, size: {}", page, size);
        PagedResponse<OnlineRetailDTO> response = service.getAllData(page, size, sortBy);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/mysql/retail/search
     * Tìm kiếm theo keyword
     */
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<OnlineRetailDTO>> searchData(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("API MySQL: Search retail data - keyword: {}", keyword);
        PagedResponse<OnlineRetailDTO> response = service.searchData(keyword, page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/mysql/retail/invoice/{invoiceNo}
     * Lấy chi tiết hóa đơn
     */
    @GetMapping("/invoice/{invoiceNo}")
    public ResponseEntity<List<OnlineRetailDTO>> getByInvoice(@PathVariable String invoiceNo) {
        log.info("API MySQL: Get retail data by invoice: {}", invoiceNo);
        List<OnlineRetailDTO> data = service.getByInvoiceNo(invoiceNo);
        return ResponseEntity.ok(data);
    }
    
    /**
     * GET /api/mysql/retail/customer/{customerId}
     * Lấy dữ liệu theo customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PagedResponse<OnlineRetailDTO>> getByCustomer(
            @PathVariable Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("API MySQL: Get retail data by customer: {}", customerId);
        PagedResponse<OnlineRetailDTO> response = service.getByCustomerId(customerId, page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/mysql/retail/country/{country}
     * Lấy dữ liệu theo quốc gia
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<PagedResponse<OnlineRetailDTO>> getByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("API MySQL: Get retail data by country: {}", country);
        PagedResponse<OnlineRetailDTO> response = service.getByCountry(country, page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/mysql/retail/product/{stockCode}
     * Lấy dữ liệu theo sản phẩm
     */
    @GetMapping("/product/{stockCode}")
    public ResponseEntity<List<OnlineRetailDTO>> getByProduct(@PathVariable String stockCode) {
        log.info("API MySQL: Get retail data by stock code: {}", stockCode);
        List<OnlineRetailDTO> data = service.getByStockCode(stockCode);
        return ResponseEntity.ok(data);
    }
    
    /**
     * GET /api/mysql/retail/date-range
     * Lấy dữ liệu theo khoảng thời gian
     */
    @GetMapping("/date-range")
    public ResponseEntity<PagedResponse<OnlineRetailDTO>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("API MySQL: Get retail data by date range: {} to {}", startDate, endDate);
        PagedResponse<OnlineRetailDTO> response = service.getByDateRange(startDate, endDate, page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/mysql/retail/stats
     * Lấy thống kê tổng quan
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("API MySQL: Get retail statistics");
        Map<String, Object> stats = service.getOverallStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * GET /api/mysql/retail/countries
     * Lấy danh sách tất cả quốc gia
     */
    @GetMapping("/countries")
    public ResponseEntity<List<String>> getAllCountries() {
        log.info("API MySQL: Get all countries");
        List<String> countries = service.getAllCountries();
        return ResponseEntity.ok(countries);
    }
    
    /**
     * GET /api/mysql/retail/top-customers
     * Lấy top khách hàng
     */
    @GetMapping("/top-customers")
    public ResponseEntity<List<Map<String, Object>>> getTopCustomers(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("API MySQL: Get top {} customers", limit);
        List<Map<String, Object>> customers = service.getTopCustomers(limit);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * GET /api/mysql/retail/top-products
     * Lấy top sản phẩm
     */
    @GetMapping("/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("API MySQL: Get top {} products", limit);
        List<Map<String, Object>> products = service.getTopProducts(limit);
        return ResponseEntity.ok(products);
    }
    
    /**
     * GET /api/mysql/retail/stats-by-country
     * Thống kê theo quốc gia
     */
    @GetMapping("/stats-by-country")
    public ResponseEntity<List<Map<String, Object>>> getStatsByCountry() {
        log.info("API MySQL: Get statistics by country");
        List<Map<String, Object>> stats = service.getStatsByCountry();
        return ResponseEntity.ok(stats);
    }
}
