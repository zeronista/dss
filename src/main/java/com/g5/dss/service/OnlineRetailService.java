package com.g5.dss.service;

import com.g5.dss.domain.mongo.OnlineRetailDocument;
import com.g5.dss.dto.OnlineRetailDTO;
import com.g5.dss.dto.PagedResponse;
import com.g5.dss.repository.mongo.OnlineRetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service để xử lý Online Retail data từ MongoDB
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OnlineRetailService {
    
    private final OnlineRetailRepository repository;
    
    /**
     * Lấy tất cả dữ liệu với phân trang
     */
    public PagedResponse<OnlineRetailDTO> getAllData(int page, int size, String sortBy) {
        log.info("Fetching online retail data - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<OnlineRetailDocument> dataPage = repository.findAll(pageable);
        
        return mapToPagedResponse(dataPage);
    }
    
    /**
     * Tìm kiếm theo keyword
     */
    public PagedResponse<OnlineRetailDTO> searchData(String keyword, int page, int size) {
        log.info("Searching online retail data with keyword: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OnlineRetailDocument> dataPage = repository.searchByKeyword(keyword, pageable);
        
        return mapToPagedResponse(dataPage);
    }
    
    /**
     * Lấy dữ liệu theo invoice number
     */
    public List<OnlineRetailDTO> getByInvoiceNo(String invoiceNo) {
        log.info("Fetching data for invoice: {}", invoiceNo);
        
        List<OnlineRetailDocument> documents = repository.findByInvoiceNo(invoiceNo);
        return documents.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy dữ liệu theo customer ID
     */
    public PagedResponse<OnlineRetailDTO> getByCustomerId(Integer customerId, int page, int size) {
        log.info("Fetching data for customer: {}", customerId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("invoiceDate").descending());
        Page<OnlineRetailDocument> dataPage = repository.findByCustomerId(customerId, pageable);
        
        return mapToPagedResponse(dataPage);
    }
    
    /**
     * Lấy dữ liệu theo country
     */
    public PagedResponse<OnlineRetailDTO> getByCountry(String country, int page, int size) {
        log.info("Fetching data for country: {}", country);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OnlineRetailDocument> dataPage = repository.findByCountry(country, pageable);
        
        return mapToPagedResponse(dataPage);
    }
    
    /**
     * Lấy dữ liệu theo product (stock code)
     */
    public List<OnlineRetailDTO> getByStockCode(String stockCode) {
        log.info("Fetching data for stock code: {}", stockCode);
        
        List<OnlineRetailDocument> documents = repository.findByStockCode(stockCode);
        return documents.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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
        return repository.findDistinctCountries().stream()
                .map(OnlineRetailDocument::getCountry)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Map Document to DTO
     */
    private OnlineRetailDTO mapToDTO(OnlineRetailDocument doc) {
        return OnlineRetailDTO.builder()
                .id(doc.getId())
                .invoiceNo(doc.getInvoiceNo())
                .stockCode(doc.getStockCode())
                .description(doc.getDescription())
                .quantity(doc.getQuantity())
                .invoiceDate(doc.getInvoiceDate())
                .unitPrice(doc.getUnitPrice())
                .customerId(doc.getCustomerId())
                .country(doc.getCountry())
                .totalAmount(doc.getTotalAmount())
                .build();
    }
    
    /**
     * Map Page to PagedResponse
     */
    private PagedResponse<OnlineRetailDTO> mapToPagedResponse(Page<OnlineRetailDocument> page) {
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
