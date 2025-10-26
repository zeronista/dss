package com.g5.dss.repository.jpa;

import com.g5.dss.domain.jpa.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByProductId(String productId);
    
    List<Product> findByCategory(String category);
    
    List<Product> findByStockQuantityLessThan(Integer threshold);
}
