package com.g5.dss.repository;

import com.g5.dss.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    Optional<Order> findByOrderId(String orderId);
    
    List<Order> findByCustomerId(String customerId);
    
    List<Order> findByStatus(String status);
    
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<Order> findByIsReturned(Boolean isReturned);
}

