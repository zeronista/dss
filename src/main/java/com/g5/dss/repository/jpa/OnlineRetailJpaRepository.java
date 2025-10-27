package com.g5.dss.repository.jpa;

import com.g5.dss.domain.jpa.OnlineRetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository cho MySQL table online_retail
 */
@Repository
public interface OnlineRetailJpaRepository extends JpaRepository<OnlineRetail, Long> {
    
    // Tìm theo invoice number
    List<OnlineRetail> findByInvoiceNo(String invoiceNo);
    
    // Tìm theo customer ID với phân trang
    Page<OnlineRetail> findByCustomerId(Integer customerId, Pageable pageable);
    
    // Tìm theo country với phân trang
    Page<OnlineRetail> findByCountry(String country, Pageable pageable);
    
    // Tìm theo stock code
    List<OnlineRetail> findByStockCode(String stockCode);
    
    // Tìm theo description (contains)
    Page<OnlineRetail> findByDescriptionContainingIgnoreCase(String keyword, Pageable pageable);
    
    // Đếm theo country
    long countByCountry(String country);
    
    // Đếm theo customer
    long countByCustomerId(Integer customerId);
    
    // Tìm theo khoảng thời gian
    Page<OnlineRetail> findByInvoiceDateBetween(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
    
    // Search tổng hợp
    @Query("SELECT o FROM OnlineRetail o WHERE " +
           "LOWER(o.invoiceNo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.stockCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<OnlineRetail> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Lấy danh sách quốc gia (distinct)
    @Query("SELECT DISTINCT o.country FROM OnlineRetail o ORDER BY o.country")
    List<String> findDistinctCountries();
    
    // Lấy top khách hàng theo tổng chi tiêu
    @Query("SELECT o.customerId, SUM(o.quantity * o.unitPrice) as total " +
           "FROM OnlineRetail o " +
           "WHERE o.customerId IS NOT NULL " +
           "GROUP BY o.customerId " +
           "ORDER BY total DESC")
    List<Object[]> findTopCustomers(Pageable pageable);
    
    // Lấy top sản phẩm bán chạy
    @Query("SELECT o.stockCode, o.description, SUM(o.quantity) as totalQty " +
           "FROM OnlineRetail o " +
           "GROUP BY o.stockCode, o.description " +
           "ORDER BY totalQty DESC")
    List<Object[]> findTopProducts(Pageable pageable);
    
    // Thống kê theo quốc gia
    @Query("SELECT o.country, COUNT(o), SUM(o.quantity * o.unitPrice) " +
           "FROM OnlineRetail o " +
           "GROUP BY o.country " +
           "ORDER BY COUNT(o) DESC")
    List<Object[]> getStatsByCountry();
}
