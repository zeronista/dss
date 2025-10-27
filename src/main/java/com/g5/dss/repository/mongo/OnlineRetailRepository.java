package com.g5.dss.repository.mongo;

import com.g5.dss.domain.mongo.OnlineRetailDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho Online Retail MongoDB data
 */
@Repository
public interface OnlineRetailRepository extends MongoRepository<OnlineRetailDocument, String> {
    
    // Tìm theo invoice number
    List<OnlineRetailDocument> findByInvoiceNo(String invoiceNo);
    
    // Tìm theo customer ID
    Page<OnlineRetailDocument> findByCustomerId(Integer customerId, Pageable pageable);
    
    // Tìm theo country
    Page<OnlineRetailDocument> findByCountry(String country, Pageable pageable);
    
    // Tìm theo stock code
    List<OnlineRetailDocument> findByStockCode(String stockCode);
    
    // Tìm theo description (contains)
    Page<OnlineRetailDocument> findByDescriptionContainingIgnoreCase(String keyword, Pageable pageable);
    
    // Đếm theo country
    long countByCountry(String country);
    
    // Đếm theo customer
    long countByCustomerId(Integer customerId);
    
    // Custom query - Tìm theo nhiều điều kiện
    @Query("{ $or: [ " +
           "{ 'InvoiceNo': ?0 }, " +
           "{ 'StockCode': ?0 }, " +
           "{ 'Description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<OnlineRetailDocument> searchByKeyword(String keyword, Pageable pageable);
    
    // Lấy tất cả quốc gia (distinct)
    @Query(value = "{}", fields = "{ 'Country': 1 }")
    List<OnlineRetailDocument> findDistinctCountries();
}
