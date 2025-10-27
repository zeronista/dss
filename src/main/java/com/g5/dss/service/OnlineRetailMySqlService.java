package com.g5.dss.service;

import com.g5.dss.domain.jpa.OnlineRetail;
import com.g5.dss.dto.OnlineRetailDTO;
import com.g5.dss.dto.PagedResponse;
import com.g5.dss.repository.jpa.OnlineRetailJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service để xử lý Online Retail data từ MySQL
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OnlineRetailMySqlService {
    
    private final OnlineRetailJpaRepository repository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Lấy tất cả dữ liệu với phân trang
     */
    public PagedResponse<OnlineRetailDTO> getAllData(int page, int size, String sortBy) {
        log.info("Fetching MySQL retail data - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<OnlineRetail> dataPage = repository.findAll(pageable);
        
        return mapToPagedResponse(dataPage);
    }
    
    /**
     * Tìm kiếm theo keyword
     */
    public PagedResponse<OnlineRetailDTO> searchData(String keyword, int page, int size) {
        log.info("Searching MySQL retail data with keyword: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OnlineRetail> dataPage = repository.searchByKeyword(keyword, pageable);
        
        return mapToPagedResponse(dataPage);
    }
    
    /**
     * Lấy dữ liệu theo invoice number
     */
    public List<OnlineRetailDTO> getByInvoiceNo(String invoiceNo) {
        log.info("Fetching MySQL data for invoice: {}", invoiceNo);
        
        List<OnlineRetail> entities = repository.findByInvoiceNo(invoiceNo);
        return entities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy dữ liệu theo customer ID
     */
    public PagedResponse<OnlineRetailDTO> getByCustomerId(Integer customerId, int page, int size) {
        log.info("Fetching MySQL data for customer: {}", customerId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("invoiceDate").descending());
        Page<OnlineRetail> dataPage = repository.findByCustomerId(customerId, pageable);
        
        return mapToPagedResponse(dataPage);
    }
    
    /**
     * Lấy dữ liệu theo country
     */
    public PagedResponse<OnlineRetailDTO> getByCountry(String country, int page, int size) {
        log.info("Fetching MySQL data for country: {}", country);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OnlineRetail> dataPage = repository.findByCountry(country, pageable);
        
        return mapToPagedResponse(dataPage);
    }
    
    /**
     * Lấy dữ liệu theo product (stock code)
     */
    public List<OnlineRetailDTO> getByStockCode(String stockCode) {
        log.info("Fetching MySQL data for stock code: {}", stockCode);
        
        List<OnlineRetail> entities = repository.findByStockCode(stockCode);
        return entities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy dữ liệu theo khoảng thời gian
     */
    public PagedResponse<OnlineRetailDTO> getByDateRange(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            int page, 
            int size) {
        log.info("Fetching MySQL data between {} and {}", startDate, endDate);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("invoiceDate").descending());
        Page<OnlineRetail> dataPage = repository.findByInvoiceDateBetween(startDate, endDate, pageable);
        
        return mapToPagedResponse(dataPage);
    }
    
    /**
     * Đếm tổng số records
     */
    public long getTotalCount() {
        return repository.count();
    }
    
    /**
     * Đếm theo country
     */
    public long countByCountry(String country) {
        return repository.countByCountry(country);
    }
    
    /**
     * Lấy danh sách countries
     */
    public List<String> getAllCountries() {
        return repository.findDistinctCountries();
    }
    
    /**
     * Lấy top khách hàng
     */
    public List<Map<String, Object>> getTopCustomers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = repository.findTopCustomers(pageable);
        
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("customerId", row[0]);
            map.put("totalSpent", row[1]);
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * Lấy top sản phẩm
     */
    public List<Map<String, Object>> getTopProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = repository.findTopProducts(pageable);
        
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("stockCode", row[0]);
            map.put("description", row[1]);
            map.put("totalQuantity", row[2]);
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * Thống kê theo quốc gia
     */
    public List<Map<String, Object>> getStatsByCountry() {
        List<Object[]> results = repository.getStatsByCountry();
        
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("country", row[0]);
            map.put("orderCount", row[1]);
            map.put("totalRevenue", row[2]);
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * Lấy thống kê tổng quan
     */
    public Map<String, Object> getOverallStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalRecords", repository.count());
        stats.put("countries", getAllCountries());
        stats.put("topCustomers", getTopCustomers(10));
        stats.put("topProducts", getTopProducts(10));
        stats.put("countryStats", getStatsByCountry());
        
        return stats;
    }
    
    /**
     * Map Entity to DTO
     */
    private OnlineRetailDTO mapToDTO(OnlineRetail entity) {
        return OnlineRetailDTO.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .invoiceNo(entity.getInvoiceNo())
                .stockCode(entity.getStockCode())
                .description(entity.getDescription())
                .quantity(entity.getQuantity())
                .invoiceDate(entity.getInvoiceDate() != null ? 
                    entity.getInvoiceDate().format(DATE_FORMATTER) : null)
                .unitPrice(entity.getUnitPrice() != null ? 
                    entity.getUnitPrice().doubleValue() : null)
                .customerId(entity.getCustomerId())
                .country(entity.getCountry())
                .totalAmount(entity.getTotalAmount() != null ? 
                    entity.getTotalAmount().doubleValue() : null)
                .build();
    }
    
    /**
     * Map Page to PagedResponse
     */
    private PagedResponse<OnlineRetailDTO> mapToPagedResponse(Page<OnlineRetail> page) {
        List<OnlineRetailDTO> content = page.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        return PagedResponse.<OnlineRetailDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
