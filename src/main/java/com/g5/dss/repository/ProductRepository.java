package com.g5.dss.repository;

import com.g5.dss.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    Optional<Product> findByProductId(String productId);
    
    List<Product> findByCategory(String category);
    
    List<Product> findByStockQuantityLessThan(Integer threshold);
}

